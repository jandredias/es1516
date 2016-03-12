package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.NotDirectoryException;

public class MyDrive extends MyDrive_Base {

	static final Logger log = LogManager.getRootLogger();

	public static MyDrive getInstance() {
		MyDrive md = FenixFramework.getDomainRoot().getMyDrive();
		if (md != null)
			return md;

		log.trace("new MyDrive");
		return new MyDrive();
	}

	private MyDrive() {
		setRoot(FenixFramework.getDomainRoot());
	}

	/**
	 * recebe um path (string) e devolve o ficheiro correspondente caso exista
	 * 
	 * @param path
	 *            absolute path to file
	 * @return the file at the specified path
	 * @see File
	 */
	public File getFileFromPath(String path) {
		ArrayList<String> pieces = splitString(path);
		File currentFile = getRootDirectory();
		for (String currentPiece : pieces) {
			try {
				currentFile = currentFile.getFile(currentPiece);
			} catch (NotDirectoryException e) {
				return null;
			}
		}
		return currentFile;
	}

	private ArrayList<String> splitString(String s) {
		ArrayList<String> pieces = new ArrayList<String>(Arrays.asList(s.split("/")));
		return pieces;
	}

	public ArrayList<String> listDir(String path) throws UnsupportedOperationException {
		File file = getFileFromPath(path);
		ListDirVisitor visitor = new ListDirVisitor();
		file.accept(visitor);
		return visitor.getFileNames();
	}
	
	public String getFileContents(File file) throws UnsupportedOperationException {
		if(!(file instanceof PlainFile)){
			throw new UnsupportedOperationException("Can only get the contents of a plain file");
		}
		PlainFile plainFile = (PlainFile) file;
		return plainFile.getContent();
	}
}
