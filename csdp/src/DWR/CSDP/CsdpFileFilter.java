package DWR.CSDP;

import java.io.File;

public class CsdpFileFilter extends javax.swing.filechooser.FileFilter {

	String[] _extensions;
	int _numExtensions;

	public CsdpFileFilter(String[] extensions, int numExtensions) {
		_extensions = extensions;
		_numExtensions = numExtensions;
	}

	// Accept all directories and all file types
	public boolean accept(File f) {
		boolean ok = false;
		if (f.isDirectory()) {
			return true;
		}
		String s = f.getName();
		int i = s.lastIndexOf(".");
		if (i > 0 && i < s.length() - 1) {
			String extension = s.substring(i + 1).toLowerCase();

			for (int index = 0; index <= _numExtensions - 1; index++) {
				if (_extensions[index].equals(extension))
					ok = true;
			}
		}
		return ok;
	}

	// The description of this filter
	public String getDescription() {
		String line = null;
		line = "Filetypes: ";
		for (int index = 0; index <= _numExtensions - 1; index++) {
			if (index < _numExtensions - 1) {
				line += "*." + _extensions[index] + ", ";
			} else {
				line += "*." + _extensions[index];
			} // else
		} // for
		return line;
	}
	// JFileChooser filechooser = new JFileChooser();
	// filechooser.addChoosableFileFilter(new MyFilter());

}// CsdpFileFilter
