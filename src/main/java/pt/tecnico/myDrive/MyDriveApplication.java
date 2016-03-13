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

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.NotDirectoryException;

public class MyDriveApplication {
	static final Logger log = LogManager.getRootLogger();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		try {
			if(args.length==0){
				
			}
			else{
				for (String s: args) xmlScan(new File(s));
			}
		    setup();
		    
		} finally { FenixFramework.shutdown(); }
	
	}
	
	
   /* public static void init() {
        log.trace("Init: " + FenixFramework.getDomainRoot());
	MyDrive.getInstance().cleanup();
    }*/

   
    public static void setup() { 
	    log.trace("Setup: " + FenixFramework.getDomainRoot());
	    
		MyDrive md = MyDrive.getInstance();
		//1
		User rootUsr = md.getRootUser();
		Directory dir = md.getRootDirectory();
		dir = dir.getDirectory("home");
		md.setFileId(md.getFileId()+1);
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
			bin.deleteFile();
		}
		catch (NotDirectoryException e){ log.trace("NotDirectoryException " + e.getMessage());}
		catch (DirectoryIsNotEmptyException e){ log.trace("DirectoryIsNotEmptyException " + e.getMessage());}
		
		//5 TODO
		xmlPrint();
		
		//6
		try{
			file.deleteFile();
		}
		catch (NotDirectoryException e){ log.trace("NotDirectoryException " + e.getMessage());}
		catch (DirectoryIsNotEmptyException e){ log.trace("DirectoryIsNotEmptyException " + e.getMessage());}
		
		//7 
		dir = md.getRootDirectory().getDirectory("home");
		ListDirVisitor v = new ListDirVisitor();
		try{
			dir.accept(v);
		} catch (pt.tecnico.myDrive.exception.UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			log.debug("UnsupportedOperationException: " + e.getMessage());
		}
		System.out.println("Directory Listing: " + v.getFileNames());
        
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
