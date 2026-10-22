package DWR.CSDP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * Read network data from a YAML file into the same parsed-data structures used by
 * the existing ASCII importer.
 */
public class NetworkYmlInput extends NetworkInput {

	private InputStreamReader _yamlIn;

	@Override
	protected void open() {
		try {
			if (!_directory.endsWith(File.separator)) {
				_directory += File.separator;
			}
			_yamlIn = new InputStreamReader(
					new FileInputStream(_directory + _filename + "." + _filetype), "UTF-8");
		} catch (IOException e) {
			throw new IllegalStateException(
					"Unable to open network YAML file " + _directory + _filename + "." + _filetype, e);
		}
	}

	@Override
	protected void read() {
		try {
			Iterable<Object> documents = new Yaml(new SafeConstructor()).loadAll(_yamlIn);
			List<Map<?, ?>> roots = new ArrayList<Map<?, ?>>();
			CsdpFileMetadata networkMetadata = null;

			for (Object document : documents) {
				if (document == null) {
					continue;
				}
				Map<?, ?> root = asMap(document, "document");
				roots.add(root);
				if (networkMetadata == null && root.containsKey("metadata")) {
					networkMetadata = readMetadata(asMap(root.get("metadata"), "metadata"));
				}
			}

			if (networkMetadata == null) {
				report("Network YAML file has no metadata. UTM Zone 10 Nad 27 and NGVD29 will be assumed");
				networkMetadata = new CsdpFileMetadata();
				networkMetadata.setToDefault();
			}
			CsdpFunctions.setNetworkMetadata(networkMetadata);

			for (Map<?, ?> root : roots) {
				Object centerlines = root.get("centerlines");
				if (centerlines == null) {
					continue;
				}
				int centerlineIndex = 0;
				for (Object value : asList(centerlines, "centerlines")) {
					readCenterline(asMap(value, "centerlines[" + centerlineIndex + "]"));
					centerlineIndex++;
				}
			}
		} catch (YAMLException | IllegalArgumentException e) {
			throw new IllegalStateException("Unable to parse network YAML file " + _directory + _filename
					+ "." + _filetype + ": " + e.getMessage(), e);
		} finally {
			close();
		}
	}

	@Override
	protected void close() {
		if (_yamlIn == null) {
			return;
		}
		try {
			_yamlIn.close();
			_yamlIn = null;
		} catch (IOException e) {
			throw new IllegalStateException("Unable to close network YAML file " + _directory + _filename
					+ "." + _filetype, e);
		}
	}

	private CsdpFileMetadata readMetadata(Map<?, ?> values) {
		CsdpFileMetadata metadata = new CsdpFileMetadata();
		setHorizontalDatum(metadata, values.get("horizontal_datum"));
		Object horizontalZone = values.get("horizontal_zone");
		if (horizontalZone != null) {
			metadata.setHZone(parseInteger(horizontalZone, "metadata.horizontal_zone"));
		}

		String horizontalUnits = stringValue(values.get("horizontal_units"));
		if ("meters".equalsIgnoreCase(horizontalUnits)) {
			metadata.setHUnits(CsdpFileMetadata.METERS);
		} else if ("feet".equalsIgnoreCase(horizontalUnits)
				|| "ussurveyfeet".equalsIgnoreCase(horizontalUnits)) {
			metadata.setHUnits(CsdpFileMetadata.USSURVEYFEET);
		}

		String verticalDatum = stringValue(values.get("vertical_datum"));
		if ("NAVD88".equalsIgnoreCase(verticalDatum)) {
			metadata.setVDatum(CsdpFileMetadata.NAVD1988);
		} else if ("NGVD29".equalsIgnoreCase(verticalDatum)) {
			metadata.setVDatum(CsdpFileMetadata.NGVD1929);
		}

		String verticalUnits = stringValue(values.get("vertical_units"));
		if ("meters".equalsIgnoreCase(verticalUnits)) {
			metadata.setVUnits(CsdpFileMetadata.METERS);
		} else if ("feet".equalsIgnoreCase(verticalUnits)
				|| "ussurveyfeet".equalsIgnoreCase(verticalUnits)) {
			metadata.setVUnits(CsdpFileMetadata.USSURVEYFEET);
		}

		Object numberOfCenterlines = firstPresent(values, "num_elements", "numCenterlines");
		if (numberOfCenterlines != null) {
			metadata.setNumElements(parseInteger(numberOfCenterlines, "metadata.num_elements"));
		}
		return metadata;
	}

	private void setHorizontalDatum(CsdpFileMetadata metadata, Object value) {
		String datum = stringValue(value);
		if ("UTMNAD83".equalsIgnoreCase(datum)) {
			metadata.setHDatum(CsdpFileMetadata.UTMNAD83);
		} else if ("UTMNAD27".equalsIgnoreCase(datum)) {
			metadata.setHDatum(CsdpFileMetadata.UTMNAD27);
		}
	}

	private void readCenterline(Map<?, ?> values) {
		Object nameValue = values.get("name");
		if (nameValue == null) {
			throw new IllegalArgumentException("Centerline is missing its name");
		}
		_pd.centerlineName = String.valueOf(nameValue);
		_pd.xUTM = new ResizableFloatArray();
		_pd.yUTM = new ResizableFloatArray();

		Object centerlinePoints = firstPresent(values, "points", "centerlinePoints");
		if (centerlinePoints == null) {
			throw new IllegalArgumentException("Centerline " + _pd.centerlineName + " is missing its points");
		}
		int pointIndex = 0;
		for (Object point : asList(centerlinePoints, "centerlines." + _pd.centerlineName + ".points")) {
			List<?> coordinates = asList(point, "centerline point");
			if (coordinates.size() != 2) {
				throw new IllegalArgumentException("Centerline points must contain exactly two coordinates");
			}
			_pd.xUTM.put(pointIndex, parseFloat(coordinates.get(0), "centerline x coordinate"));
			_pd.yUTM.put(pointIndex, parseFloat(coordinates.get(1), "centerline y coordinate"));
			pointIndex++;
		}
		_pd.numCenterlinePoints = pointIndex;
		storeCenterline();

		Object xsects = values.get("xsects");
		if (xsects == null) {
			_pd.numXsect = 0;
			return;
		}

		int xsectCount = 0;
		for (Object value : asList(xsects, "centerlines." + _pd.centerlineName + ".xsects")) {
			Map<?, ?> xsect = asMap(value, "cross-section");
			readXsect(xsect);
			xsectCount++;
		}
		_pd.numXsect = xsectCount;
	}

	private void readXsect(Map<?, ?> values) {
		Object distance = firstPresent(values, "xsectDistance", "station");
		if (distance == null) {
			throw new IllegalArgumentException("Cross-section is missing xsectDistance/station");
		}
		_pd.distAlongCenterline = parseFloat(distance, "cross-section station");
		Object length = firstPresent(values, "xsectLineLength", "length");
		_pd.xsectLineLength = length == null ? 0.0f : parseFloat(length, "cross-section line length");
		Object metadata = firstPresent(values, "xsectMetadata", "note");
		_pd.metadata = metadata == null ? "" : String.valueOf(metadata);
		_pd.station = new ResizableFloatArray();
		_pd.elevation = new ResizableFloatArray();

		Object points = firstPresent(values, "xsectPoints", "points");
		if (points == null) {
			throw new IllegalArgumentException("Cross-section is missing xsectPoints/points");
		}
		int pointIndex = 0;
		for (Object point : asList(points, "cross-section points")) {
			List<?> coordinates = asList(point, "cross-section point");
			if (coordinates.size() != 2) {
				throw new IllegalArgumentException("Cross-section points must contain station and elevation");
			}
			_pd.station.put(pointIndex, parseFloat(coordinates.get(0), "cross-section point station"));
			_pd.elevation.put(pointIndex, parseFloat(coordinates.get(1), "cross-section point elevation"));
			pointIndex++;
		}
		_pd.numXsectPoints = pointIndex;

		Centerline centerline = _net.getCenterline(_pd.centerlineName);
		storeXsectLine(centerline.getNumXsects());
	}

	private Object firstPresent(Map<?, ?> values, String firstKey, String secondKey) {
		return values.containsKey(firstKey) ? values.get(firstKey) : values.get(secondKey);
	}

	private Map<?, ?> asMap(Object value, String context) {
		if (!(value instanceof Map<?, ?>)) {
			throw new IllegalArgumentException("Expected a mapping for " + context);
		}
		return (Map<?, ?>) value;
	}

	private List<?> asList(Object value, String context) {
		if (value == null) {
			return java.util.Collections.emptyList();
		}
		if (!(value instanceof List<?>)) {
			throw new IllegalArgumentException("Expected a list for " + context);
		}
		return (List<?>) value;
	}

	private int parseInteger(Object value, String context) {
		try {
			return Integer.parseInt(String.valueOf(value));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid integer for " + context + ": " + value, e);
		}
	}

	private float parseFloat(Object value, String context) {
		try {
			return value instanceof Number ? ((Number) value).floatValue() : Float.parseFloat(String.valueOf(value));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid number for " + context + ": " + value, e);
		}
	}

	private String stringValue(Object value) {
		return value == null ? "" : String.valueOf(value);
	}

	private void report(String message) {
		if (_gui == null) {
			System.err.println(message);
		} else {
			javax.swing.JOptionPane.showMessageDialog(_gui, message, "No Metadata",
					javax.swing.JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
