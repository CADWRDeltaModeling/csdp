package DWR.CSDP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes network data to a YAML file.
 */
public class NetworkYmlOutput extends NetworkOutput {

	private Network _net;
	private Path _outputFile;

	NetworkYmlOutput(Network net) {
		_net = net;
	}

	@Override
	protected void open() {
		_outputFile = new File(_directory + _filename + "." + YML_TYPE).toPath();
	}

	@Override
	protected boolean write() {
		int numCenterlinesWritten = 0;
		try (BufferedWriter writer = Files.newBufferedWriter(_outputFile, StandardCharsets.UTF_8)) {
			writeNetwork(writer);
			for (int i = 0; i < _net.getNumCenterlines(); i++) {
				String name = _net.getCenterlineName(i);
				if (name.length() > 0 && (_channelsToExport == null || _channelsToExport.contains(name))) {
					numCenterlinesWritten++;
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Unable to write complete network YAML file " + _outputFile, e);
		}
		System.out.println("Wrote yaml file to " + _outputFile);
		System.out.println("numCenterlinesWritten="+numCenterlinesWritten);
		_net.setIsUpdated(false);
		_channelsToExport = null;
		return true;
	}

	private void writeNetwork(BufferedWriter writer) throws IOException {
		CsdpFileMetadata metadata = CsdpFunctions.getNetworkMetadata();
		writer.write("---\nmetadata:\n");
		writeScalar(writer, "horizontal_datum", metadata.getHDatumString(), "  ");
		writeScalar(writer, "horizontal_zone", Integer.toString(metadata.getHZone()), "  ");
		writeScalar(writer, "horizontal_units", metadata.getHUnitsString(), "  ");
		writeScalar(writer, "vertical_datum", metadata.getVDatumString(), "  ");
		writeScalar(writer, "vertical_units", metadata.getVUnitsString(), "  ");
		writeScalar(writer, "filetype", "network", "  ");
		writer.write("centerlines:\n");

		_net.sortCenterlineNames();
		for (int i = 0; i < _net.getNumCenterlines(); i++) {
			if (_net.getCenterlineName(i).length() == 0) {
				_net.removeCenterline(_net.getCenterlineName(i));
				i = -1;
			}
		}
		for (int i = 0; i < _net.getNumCenterlines(); i++) {
			String name = _net.getCenterlineName(i);
			if (name.length() == 0) {
				continue;
			}
			if (_channelsToExport != null && !_channelsToExport.contains(name)) {
				continue;
			}
			writeCenterline(writer, name, _net.getCenterline(name));
		}
	}

	private void writeCenterline(BufferedWriter writer, String name, Centerline centerline) throws IOException {
		writer.write("  - name: ");
		writeQuoted(writer, name);
		writer.write("\n");
		writeCenterlinePoints(writer, centerline);
		writer.write("    xsects:\n");
		for (int i = 0; i < centerline.getNumXsects(); i++) {
			Xsect xsect = centerline.getXsect(i);
			writer.write("      - xsectDistance: ");
			writer.write(Double.toString(xsect.getDistAlongCenterlineFeet()));
			writer.write("\n        xsectLineLength: ");
			writer.write(Double.toString(xsect.getXsectLineLengthFeet()));
			writer.write("\n        xsectMetadata: ");
			writeQuoted(writer, xsect.getMetadata() == null ? "" : xsect.getMetadata());
			writer.write("\n");
			writeXsectPoints(writer, xsect);
		}
	}

	private void writeCenterlinePoints(BufferedWriter writer, Centerline centerline) throws IOException {
		writer.write("    centerlinePoints:");
		if (centerline.getNumCenterlinePoints() == 0) {
			writer.write(" []\n");
			return;
		}
		writer.write("\n");
		for (int i = 0; i < centerline.getNumCenterlinePoints(); i++) {
			CenterlinePoint point = centerline.getCenterlinePoint(i);
			writePoint(writer, "      ", point.getXFeet(), point.getYFeet());
		}
	}

	private void writeXsectPoints(BufferedWriter writer, Xsect xsect) throws IOException {
		writer.write("        xsectPoints:");
		if (xsect.getNumPoints() == 0) {
			writer.write(" []\n");
			return;
		}
		writer.write("\n");
		for (int i = 0; i < xsect.getNumPoints(); i++) {
			XsectPoint point = xsect.getXsectPoint(i);
			writePoint(writer, "          ", point.getStationFeet(), point.getElevationFeet());
		}
	}

	private void writePoint(BufferedWriter writer, String indentation, double x, double y) throws IOException {
		writer.write(indentation);
		writer.write("- [");
		writer.write(Double.toString(x));
		writer.write(", ");
		writer.write(Double.toString(y));
		writer.write("]\n");
	}

	private void writeScalar(BufferedWriter writer, String key, String value, String indentation)
			throws IOException {
		writer.write(indentation);
		writer.write(key);
		writer.write(": ");
		writeQuoted(writer, value == null ? "" : value);
		writer.write("\n");
	}

	private void writeQuoted(BufferedWriter writer, String value) throws IOException {
		writer.write('"');
		for (int i = 0; i < value.length(); i++) {
			char character = value.charAt(i);
			switch (character) {
			case '\\':
				writer.write("\\\\");
				break;
			case '"':
				writer.write("\\\"");
				break;
			case '\n':
				writer.write("\\n");
				break;
			case '\r':
				writer.write("\\r");
				break;
			case '\t':
				writer.write("\\t");
				break;
			default:
				if (Character.isISOControl(character)) {
					writer.write(String.format("\\u%04X", (int) character));
				} else {
					writer.write(character);
				}
			}
		}
		writer.write('"');
	}

	@Override
	protected void close() {
	}
}
