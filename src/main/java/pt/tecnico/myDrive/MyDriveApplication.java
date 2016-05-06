package pt.tecnico.myDrive;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.UserDoesNotExistsException;
import pt.tecnico.myDrive.exception.UsernameAlreadyInUseException;

public class MyDriveApplication {
	static final Logger log = LogManager.getRootLogger();

	public static void main(String[] args) throws IOException {
		// if(helloWorld())
		// return;
		try {
			init();

			// Parse the arguments
			for (String s : args)
				xmlScan(new File(s));
			xmlPrint();
		} finally {
			FenixFramework.shutdown();
		}
	}

	public static boolean helloWorld() {

		return true;
	}

	@Atomic
	public static void init() {
		log.trace("Init: " + FenixFramework.getDomainRoot());
		MyDrive.getInstance().cleanup();
	}

	@Atomic
	public static void setup() {
		log.trace("Setup: " + FenixFramework.getDomainRoot());
		MyDrive.getInstance();
		log.trace("Setup: Create MyDrive");
	}

	/*
	 * Prints a XML output to console
	 */
	@Atomic
	public static void xmlPrint() {
		log.trace("xmlPrint: " + FenixFramework.getDomainRoot());
		MyDrive md = MyDrive.getInstance();
		Document doc = md.xmlExport();
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		try {
			xmlOutput.output(doc, new PrintStream(System.out));
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * Reads a XML file and imports the content to the file system
	 *
	 * @param File
	 */
	@Atomic
	public static void xmlScan(File file) {
		log.trace("xmlScan: " + FenixFramework.getDomainRoot());

		MyDrive md = MyDrive.getInstance();
		SAXBuilder builder = new SAXBuilder();
		try {
			log.trace("xmlScan: Importing the main document");
			Document document = (Document) builder.build(file);
			md.xmlImport(document.getRootElement());
		} catch (ImportDocumentException e) {
			e.getStackTrace();
		}
		log.trace("End of xmlScan");
	}
}
