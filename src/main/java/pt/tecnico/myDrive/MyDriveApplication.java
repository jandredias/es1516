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
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class MyDriveApplication {
	static final Logger log = LogManager.getRootLogger();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		try {
			setup();
		    for (String s: args) xmlScan(new File(s));
		    xmlPrint();
		} finally { FenixFramework.shutdown(); }
	
	}
	
	@Atomic
    public static void init() {
        log.trace("Init: " + FenixFramework.getDomainRoot());
//      MyDrive.getInstance().cleanup(); FIXME
        MyDrive.getInstance();
    }

   
    public static void setup() { 
	    log.trace("Setup: " + FenixFramework.getDomainRoot());
	    
		MyDrive md = MyDrive.getInstance();
		//System.out.println(md.toString());
		//1
		User rootUsr = md.getRootUser();
		Directory rootDir = md.getRootDirectory();
		Directory homeDir;
		try {
			homeDir = rootDir.getDirectory("home");
		} catch (pt.tecnico.myDrive.exception.FileNotFoundException e) {
			md.incrementFileId();
			homeDir = new Directory("home", md.getFileId(), new DateTime(), 11111010, rootUsr, rootDir);
		}
		
		Directory dir = homeDir;
		md.incrementFileId();
		PlainFile file = new PlainFile("README", md.getFileId(), new DateTime(), 11111011, md.getRootUser(), "lista de utilizadores");
		dir.addFiles(file);
		
		//2
		dir = md.getRootDirectory();
		md.incrementFileId();
		Directory usr = new Directory("usr", md.getFileId(), new DateTime(), 11111010, rootUsr, dir);
		md.incrementFileId();
		Directory local = new Directory("local", md.getFileId(), new DateTime(), 11111010, rootUsr, usr);
		md.incrementFileId();
		Directory bin = new Directory("bin", md.getFileId(), new DateTime(), 11111010, rootUsr, local);
		
		//3
		System.out.println(file.getContent());
		
		//4
		try{
			md.deleteFile("/usr/local/bin");
		}
		catch (DirectoryIsNotEmptyException e) {
			log.error("Cannot delete a non-empty folder");
		}
		catch (FileNotFoundException e){
			log.error("The file doesn't exist");
		}
		catch (NotDirectoryException e) {
			//Should never occur
			log.error("The father directory isn't a directory");
		}		
		//5
		xmlPrint();
		
		//6
		try{
			md.deleteFile("/home/README");
		}
		catch (DirectoryIsNotEmptyException e) {
			log.error("Cannot delete a non-empty folder");
		}
		catch (FileNotFoundException e){
			log.error("The file doesn't exist");
		}
		catch (NotDirectoryException e) {
			//Should never occur
			log.error("The father directory isn't a directory");
		}	
		
		//7 
		try {
			System.out.println("Directory Listing: " + md.listDir("/home/README"));
		} catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
        
    }
    
    public static void xmlPrint() {
        log.trace("xmlPrint: " + FenixFramework.getDomainRoot());
        Document doc = MyDrive.getInstance().xmlExport();
        XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
	try {
		xmlOutput.output(doc, new PrintStream(System.out));
	} catch (IOException e) { System.out.println(e); }
    }
    
    public static void xmlScan(File file) { } /* FIX ME
    	log.trace("xmlScan: " + FenixFramework.getDomainRoot());
    	MyDrive md = MyDrive.getInstance();
    	SAXBuilder builder = new SAXBuilder();
    	try {
    		Document document = (Document)builder.build(file);
    		md.xmlImport(document.getRootElement()); NO IMPORT METHOD IN MYDRIVE
    	} catch (JDOMException | IOException e) {
    		e.printStackTrace();
    	}
    }*/


}
