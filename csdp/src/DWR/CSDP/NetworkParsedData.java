package DWR.CSDP;

/**
 * Stores parsed data from network file
 */
public class NetworkParsedData {

	public int numCenterlines;

	public String centerlineName;
	public int numCenterlinePoints;
	public ResizableFloatArray xUTM = new ResizableFloatArray();
	public ResizableFloatArray yUTM = new ResizableFloatArray();
	public int numXsect;

	public int numXsectPoints;
	public ResizableFloatArray station = new ResizableFloatArray();
	public ResizableFloatArray elevation = new ResizableFloatArray();
	public float distAlongCenterline = 0.0f;
	public float xsectLineLength = 0.0f;
	public String metadata;
}// class NetworkParsedData
