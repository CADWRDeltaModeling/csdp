package DWR.CSDP;
import java.io.*;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 */
public class AsciiFileWriter{
	FileWriter _aOutFile = null;
	BufferedWriter _asciiOut = null;
	String _filename;
	Vector _allLines = null;
	private JFrame parent;
	boolean writeExceptionMessageShown = false;

	public AsciiFileWriter(JFrame parent, String filename){
		this.parent = parent;
		_allLines = new Vector();
		_filename=filename;
		this.writeExceptionMessageShown = false;
		open();
	}//constructor

	public AsciiFileWriter(JFrame parent, String filename, boolean append){
		this.parent = parent;
		_allLines = new Vector();
		_filename=filename;
		this.writeExceptionMessageShown = false;
		if(append){
			openAppend();
		}else{
			open();
		}
	}

	protected void openAppend(){
		try{
			_aOutFile = new FileWriter(_filename,true);
			_asciiOut = new BufferedWriter(_aOutFile);
		}catch(IOException e){
			JOptionPane.showMessageDialog(parent, "Error occurred while opening file "+_filename+"\n\n"
					+ "Is the file locked for writing?", "Error", JOptionPane.ERROR_MESSAGE);
//			System.out.println("error occurred while opening file "+_filename + e.getMessage());
		}

	}

	protected void open(){
		try{
			_aOutFile = new FileWriter(_filename);
			_asciiOut = new BufferedWriter(_aOutFile);
		}catch(IOException e){
			JOptionPane.showMessageDialog(parent, "Error occurred while opening file "+_filename+"\n\n"
					+ "Is the file locked for writing?", "Error", JOptionPane.ERROR_MESSAGE);
//			System.out.println("error occurred while opening file "+_filename + e.getMessage());
		}
	}//open

	public void writeLine(String line){
		try{
			_asciiOut.write(line);
			_asciiOut.newLine();
		}catch(Exception e){
			if(!this.writeExceptionMessageShown) {
				JOptionPane.showMessageDialog(parent, "Error occurred while trying to write to file "+_filename+"\n"
						+ "Is the file locked for writing?", "Error", JOptionPane.ERROR_MESSAGE);
				this.writeExceptionMessageShown = true;
			}
//			System.out.println("exception caught in AsciiFileWriter.write while writing ");
//			System.out.println("file "+_filename+e.getMessage());
		}
	}//writeLine

	public void close(){
		try{
			_asciiOut.close();
		}catch(java.io.IOException e){
			JOptionPane.showMessageDialog(parent, "Error occurred while trying to close file "+_filename, "Error", JOptionPane.ERROR_MESSAGE);
//			System.out.println("exception caught while trying to close file: "+
//					_filename+e.getMessage());
		}
	}//close

}//AsciiFileWriter
