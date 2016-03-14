package pt.tecnico.myDrive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.joda.time.DateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.NoSuchUserException;
import pt.tecnico.myDrive.exception.UsernameAlreadyInUseException;

public class MyDriveApplication {
  static final Logger log = LogManager.getRootLogger();

  public static void main(String[] args) throws IOException {
    //TODO
    try {
      //init();
      //for (String s: args) xmlScan(new File(s));

      setup();
      xmlPrint();
      //teste_tiago();
      //listDirectoryTest();

    } finally { FenixFramework.shutdown(); }

  }

  @Atomic
  public static void init() {
    log.trace("Init: " + FenixFramework.getDomainRoot());
    MyDrive.getInstance().cleanup();
  }

  @Atomic
  public static void setup() {
                      

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
	  } catch(JDOMException | IOException e) {
	    e.printStackTrace();
	  } catch(InvalidUsernameException e){
	    System.out.println("Invalid username on XML import file");
	  } catch(FileNotFoundException e){
	    System.out.println("File not found on XML import file");
	  } catch(NotDirectoryException e) {
	    System.out.println("Only directories can contain another files");
	  } catch(NoSuchUserException e) {
	    System.out.println("There is not such a user on XML import file");
	  }catch(FileExistsException e){
	    System.out.println("Duplicated file on XML import file");
	  }

	  log.trace("End of xmlScan");
	}

	@Atomic
	public static void listDirectoryTest() {

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

	@Atomic
	public static void teste_tiago(){}
}
