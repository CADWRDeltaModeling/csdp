package DWR.CSDP;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * Stores channel connectivity information
 */
public class DSMChannels {
	private final String CHAN_HEADER = "CHAN";
	private final String LENGTH_HEADER = "LENGTH";
	private final String DOWNNODE_HEADER = "DOWNNODE";
	private final String UPNODE_HEADER = "UPNODE";
	private final String XSECT_HEADER = "XSECT";
	private final String DIST_HEADER = "DIST";
//	private int _numChannels;

	private Vector<String> _chanNum = new Vector<String>();
	private Hashtable<String, Integer> _length = new Hashtable<String, Integer>();
	private Hashtable<String, String> _manning = new Hashtable<String, String>();
	private Hashtable<String, String> _dispersion = new Hashtable<String, String>();
	private Hashtable<String, Integer> _downnode = new Hashtable<String, Integer>();
	private Hashtable<String, Integer> _upnode = new Hashtable<String, Integer>();
	
	private Hashtable<String, Integer> _xsect1 = new Hashtable<String, Integer>();
	private Hashtable<String, Integer> _dist1 = new Hashtable<String, Integer>();
	private Hashtable<String, Integer> _xsect2 = new Hashtable<String, Integer>();
	private Hashtable<String, Integer> _dist2 = new Hashtable<String, Integer>();

//	private int _numXsectLayers = 0;
	private Vector<String> _xsectLayerID = new Vector<String>();
	private Hashtable<String, String> _xsectLayerDist = new Hashtable<String, String>();
	private Hashtable<String, String> _xsectLayerElev = new Hashtable<String, String>();
	private Hashtable<String, String> _xsectLayerArea = new Hashtable<String, String>();
	private Hashtable<String, String> _xsectLayerWidth = new Hashtable<String, String>();
	private Hashtable<String, String> _xsectLayerWetPerim = new Hashtable<String, String>();
	/*
	 * For each node, an array containing names of centerlines connected to it
	 */
	private Hashtable<Integer, List<String>> _channelsConnectedToNode = new Hashtable<Integer, List<String>>();
	
	private final String ELEV_HEADER = "ELEV";
	private final String AREA_HEADER = "AREA";
	private final String WIDTH_HEADER = "WIDTH";
	private final String WET_PERIM_HEADER = "WET_PERIM";

	private static final boolean DEBUG = false;

	public void addDSMChannel(int index, String name, int length, String manning, String dispersion, int upnode, int downnode) {
		_chanNum.addElement(name);
		_length.put(name, length);
		_manning.put(name, manning);
		_dispersion.put(name, dispersion);
		_upnode.put(name, upnode);
		_downnode.put(name, downnode);
		
		if(!_channelsConnectedToNode.containsKey(upnode)) {
			_channelsConnectedToNode.put(upnode, new ArrayList<String>());
		}
		_channelsConnectedToNode.get(upnode).add(name);
		
		if(!_channelsConnectedToNode.containsKey(downnode)) {
			_channelsConnectedToNode.put(downnode, new ArrayList<String>());
		}
		_channelsConnectedToNode.get(downnode).add(name);
	}

	public void addDSMXsectLayer(int index, String chan, String dist, String elev, String area, String width, String wetPerim) {
		String xsectLayerID = chan+"_"+dist+"_"+elev;
		_xsectLayerID.addElement(xsectLayerID);
		_xsectLayerDist.put(xsectLayerID, dist);
		_xsectLayerElev.put(xsectLayerID, elev);
		_xsectLayerArea.put(xsectLayerID, area);
		_xsectLayerWidth.put(xsectLayerID, width);
		_xsectLayerWetPerim.put(xsectLayerID, wetPerim);
	}
	
	public String getXsectLayerID(int index) {
		return _xsectLayerID.get(index);
	}
	public String getXsectDist(String xsectLayerID) {
		return _xsectLayerDist.get(xsectLayerID);
	}
	public String getXsectElev(String xsectLayerID) {
		return _xsectLayerElev.get(xsectLayerID);
	}
	public String getXsectArea(String xsectLayerID) {
		return _xsectLayerArea.get(xsectLayerID);
	}
	public String getXsectWidth(String xsectLayerID) {
		return _xsectLayerWidth.get(xsectLayerID);
	}
	public String getXsectWetPerim(String xsectLayerID) {
		return _xsectLayerWetPerim.get(xsectLayerID);
	}
	
	public int getNumXsectLayers() {
//		return _numXsectLayers;
		return _xsectLayerID.size();
	}
	
	/**
	 * returns channel number
	 */
	public String getChanNum(int index) {
		return _chanNum.get(index);
	}

	/**
	 * returns number of channels
	 */
	public int getNumChannels() {
//		return _numChannels;
		return _chanNum.size();
	}

	/**
	 * returns length
	 */
	public int getLength(String chan) {
		int length = -Integer.MAX_VALUE;
		if (_length.containsKey(chan)) {
			length = _length.get(chan).intValue();
		} else {
			System.out.println("Not writing output for channel " + chan + " because it's not in the DSM2 input file");
		}
		return length;
	}

	/**
	 * returns downnode
	 */
	public int getDownnode(String chan) {
		int returnValue = -Integer.MAX_VALUE;
		if (_downnode.containsKey(chan)) {
			returnValue = _downnode.get(chan).intValue();
		}
		return returnValue;
	}

	/**
	 * stores first rectangular xsect number
	 */
	public void putXsect1(String chan, int value) {
		Integer xsect1Object = new Integer(value);
		_xsect1.put(chan, xsect1Object);
	}

	/**
	 * Stores distance of first rectangular cross-section
	 */
	public void putDist1(String chan, int value) {
		Integer dist1Object = new Integer(value);
		_dist1.put(chan, dist1Object);
	}

	/**
	 * stores first rectangular xsect number
	 */
	public void putXsect2(String chan, int value) {
		Integer xsect2Object = new Integer(value);
		_xsect2.put(chan, xsect2Object);
	}

	/**
	 * Stores distance of first rectangular cross-section
	 */
	public void putDist2(String chan, int value) {
		Integer dist2Object = new Integer(value);
		_dist2.put(chan, dist2Object);
	}

	/**
	 * returns number of first rectangular cross-section
	 */
	public int getXsect1(String chan) {
		int returnValue = -Integer.MAX_VALUE;
		if (_xsect1.containsKey(chan)) {
			returnValue = _xsect1.get(chan).intValue();
		}
		return returnValue;
	}

	/**
	 * returns distance of first rectangular cross-section
	 */
	public int getDist1(String chan) {
		int returnValue = -Integer.MAX_VALUE;
		if (_dist1.containsKey(chan)) {
			returnValue =  _dist1.get(chan).intValue();
		}
		return returnValue;
	}

	/**
	 * returns number of second rectangular cross-section
	 */
	public int getXsect2(String chan) {
		int returnValue = -Integer.MAX_VALUE;
		if (_xsect2.containsKey(chan)) {
			returnValue = _xsect2.get(chan).intValue();
		}
		return returnValue;
	}

	/**
	 * returns distance of second rectangular cross-section
	 */
	public int getDist2(String chan) {
		int returnValue = -Integer.MAX_VALUE;
		if (_dist2.containsKey(chan)) {
			returnValue = _dist2.get(chan).intValue();
		}
		return returnValue;
	}

	/**
	 * returns upnode
	 */
	public int getUpnode(String chan) {
		if (DEBUG)
			System.out.println("chan, _upnode=" + chan + "," + _upnode);
		if (DEBUG)
			System.out.println("upnode=" + (Integer) _upnode.get(chan));
		int returnValue = -Integer.MAX_VALUE;
		if (_upnode.containsKey(chan)) {
			returnValue = _upnode.get(chan).intValue();
		}
		return returnValue;
	}

	public String getManning(String chan) {
		String returnValue = "";
		if(_manning.containsKey(chan)) {
			returnValue = _manning.get(chan);
		}
		return returnValue;
	}

	public String getDispersion(String chan) {
		String returnValue = "";
		if(_dispersion.containsKey(chan)) {
			returnValue = _dispersion.get(chan);
		}
		return returnValue;
	}

	
	/**
	 * To check if all information exists for the specified channel number
	 */
	public boolean channelExists(String chan) {
		boolean exists = false;
		if (_length.containsKey(chan) && _downnode.containsKey(chan) && _upnode.containsKey(chan))
			exists = true;
		return exists;
	}

	/*
	 * For given centerline and node number:
	 * if there are two channels connected to the node, return the name of the channel
	 * that does not match the centerlineName.
	 * If there either 1 or 3 or more channels connected to the node, return null. 
	 */
	public String getChannelConnectedToNode(String centerlineName, int node) {
		List<String> channelsList = _channelsConnectedToNode.get(node);
		String returnCenterlineName = null;
		if(channelsList.size()==2) {
			if(channelsList.get(0).equals(centerlineName)) {
				returnCenterlineName = channelsList.get(1);
			}else if(channelsList.get(1).equals(centerlineName)) {
				returnCenterlineName = channelsList.get(0);
			}else {
				System.out.println("Error in DSMChannels.getChannelConnectedToNode: given centerlineName is not in DSMChannls object!");
			}
		}
		return returnCenterlineName;
	}//getChannelConnectedToNode


}// DSMChannels
