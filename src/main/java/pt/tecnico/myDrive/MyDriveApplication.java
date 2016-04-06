package pt.tecnico.myDrive;

import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.UserDoesNotExistsException;
import pt.tecnico.myDrive.exception.UsernameAlreadyInUseException;
import java.lang.reflect.InvocationTargetException;

public class MyDriveApplication {
	static final Logger log = LogManager.getRootLogger();

	public static void main(String[] args) throws IOException {
		//TODO
		
//		if (helloWorld())
//			return;
		try {

			init();
			for (String s: args) xmlScan(new File(s));

			try {
				setup();
			} catch (InvalidFileNameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			xmlPrint();

		} finally { FenixFramework.shutdown(); }

	}

	public static boolean helloWorld(){
		DateTime currTime = new DateTime();
		DateTime limitTime = new DateTime();
		
		limitTime = limitTime.minusSeconds(1); 

		Duration duration = new Duration(currTime, limitTime);
		System.out.println("Mins: " + duration.getMillis());
		return true;
	}
	
	@Atomic
	public static void init() {
		log.trace("Init: " + FenixFramework.getDomainRoot());
		MyDrive.getInstance().cleanup();
	}

	@Atomic
	public static void setup() throws InvalidFileNameException {
		log.trace("Setup: " + FenixFramework.getDomainRoot());

		MyDrive md = MyDrive.getInstance();
		log.trace("Setup: Create MyDrive");

		/*
		step1(md);
		step2(md);
		step3(md);
		step4(md);
		step5(md);
		step6(md);
		step7(md);
		step8(md);
		step9(md);
		step10(md);
		
		//-----
		// POS ENTREGA 1
		//-----
		
		step11(md);
		*/
	}

	public static void step1(MyDrive md){
		try{
			User rootUsr = md.getRootUser();
			md.addPlainFile("/home","README",rootUsr ,"Lista De Utilizadores");

		}catch(FileNotFoundException | FileExistsException | InvalidFileNameException | PermissionDeniedException e){
			System.out.println("creating readme.. ");
			e.printStackTrace();
		}
	}
	public static void step2(MyDrive md){
		try {
			User rootUsr = md.getRootUser();
			md.addDirectory("/", "usr", rootUsr);
			md.addDirectory("/usr", "local", rootUsr);
			md.addDirectory("/usr/local", "bin", rootUsr);
		} catch (FileExistsException | FileNotFoundException | InvalidFileNameException | PermissionDeniedException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	public static void step3(MyDrive md){
		try{
			md.getFileContents("/home/README");
		}catch(FileNotFoundException | NotDirectoryException | UnsupportedOperationException e){
			e.printStackTrace();
		}
	}
	public static void step4(MyDrive md){
		try {
			md.removeFile("/usr/local/bin");
		} catch (FileNotFoundException | DirectoryIsNotEmptyException e2) {
			e2.printStackTrace();
		}
	}
	public static void step5(MyDrive md){
		//xmlPrint
	}
	public static void step6(MyDrive md){
		try{
			md.removeFile("/home/README");
		} catch (FileNotFoundException | DirectoryIsNotEmptyException e2) {
			//e2.printStackTrace();
			log.error("The file doesn't exist");
		}
	}
	public static void step7(MyDrive md){
		try {
			System.out.println("Directory Listing /home: " + md.listDir("/home"));
		} catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public static void step8(MyDrive md){
		//8
		try {
			System.out.println("Directory Listing /: " + md.listDir("/"));
		} catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public static void step9(MyDrive md){
		System.out.println("Deleting /usr");
		try {
			md.removeFile("/usr");
		} catch (DirectoryIsNotEmptyException e) {
			System.out.println("ERROR: Directory not empty");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void step10(MyDrive md){
		try {
			String folder = "/";
			System.out.println("Directory Listing "+folder+" : " + md.listDir(folder));
		} catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void step11(MyDrive md){

		try {
			md.addUser("JAO", "JAO", "JAO", "rwdxrwdx");
			User normalUsr = md.getUserByUsername("JAO");
			md.addDirectory("/home/JAO", "usr", normalUsr);
		} catch (FileExistsException | InvalidFileNameException | FileNotFoundException
				| PermissionDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidUsernameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsernameAlreadyInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
			Document document = (Document)builder.build(file);
			md.xmlImport(document.getRootElement());
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch(JDOMException | IOException e) {
			e.printStackTrace();
		} catch(InvalidUsernameException e){
			System.out.println("Invalid username on XML import file");
		} catch(FileNotFoundException e){
			e.printStackTrace();
			System.out.println("File not found on XML import file");
		} catch(NotDirectoryException e) {
			System.out.println("Only directories can contain another files");
		} catch(UserDoesNotExistsException e) {
			System.out.println("There is not such a user on XML import file");
		}catch(FileExistsException e){
			System.out.println("Duplicated file on XML import file");
		} catch (InvalidFileNameException e) {
			System.out.println("Invalid charater somewhere");
		} catch (UsernameAlreadyInUseException e) {
			System.out.println("Username cannot be used more than once");
		} catch (NoSuchMethodException e){
			e.printStackTrace();
		} catch (InstantiationException | IllegalAccessException e){
			e.printStackTrace();
		} catch( InvocationTargetException e){
			e.printStackTrace();
		} catch (PermissionDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.trace("End of xmlScan");
	}
/*
	@Atomic
	public static void listDirectoryTest() throws InvalidFileNameException {

		try {

			MyDrive md = MyDrive.getInstance();

			User rootUsr = md.getRootUser();
			Directory rootDir = md.getRootDirectory();
			Directory testDir = new Directory("testDir",  new DateTime(), 11111010, rootUsr, rootDir);
			new Directory("testDir2",  new DateTime(), 11111010, rootUsr, testDir);
			new Directory("testDir3",  new DateTime(), 11111010, rootUsr, rootDir);
			log.debug(md.listDir("/testDir"));

		} catch (FileExistsException e) {
			e.printStackTrace();
		}
		catch (UnsupportedOperationException e) {
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (NotDirectoryException e) {
			e.printStackTrace();
		}

	}
*/
	@Atomic
	public static void teste_tiago(){}
}
