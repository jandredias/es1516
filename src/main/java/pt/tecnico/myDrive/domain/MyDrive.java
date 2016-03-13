package pt.tecnico.myDrive.domain;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;

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
    	
    	Root root = new Root();
    	this.addUsers(root);
    	incrementFileId();
    	Directory rootDirectory = new Directory("/", getFileId(), new DateTime(), 11111010 , root, null);
    	this.setRootDirectory(rootDirectory);
    	rootDirectory.setDir(rootDirectory);
    	incrementFileId();
    	new Directory("root",getFileId(), new DateTime(), 11111010 , root, rootDirectory);
    	incrementFileId();
    	Directory homeFolder = new Directory("home",getFileId(), new DateTime(), 11111010 , root, rootDirectory);
    	incrementFileId();
    	Directory home_root = new Directory("root",getFileId(), new DateTime(), 11111010 , root, homeFolder);
    	
    	root.setUsersHome(home_root);
 
    }
    
    public File getFileFromPath(String path) throws FileNotFoundException, NotDirectoryException {
        ArrayList<String> pieces = splitString(path);
        File currentFile = getRootDirectory();
        for(String currentPiece: pieces){
        		currentFile = currentFile.getFile(currentPiece);
        }
        return currentFile;
    }

    private ArrayList<String> splitString(String s){
    	ArrayList<String> pieces = new ArrayList<String>(Arrays.asList(s.split("/")));
    	return pieces;
    }
	
    //FIXME return root
	public User getRootUser() {
		Directory directory = getRootDirectory();
		User root = directory.getOwner();
		return root;
	}
	
	public Document xmlExport() {
        Element element = new Element("mydrive");
        Document doc = new Document(element);
        
        for (User user: getUsersSet())
            element.addContent(user.xmlExport());
        
        Directory rootDirectory = getRootDirectory();
        element.addContent(rootDirectory.xmlExport());
        
        
        return doc;
    }

    public void addUser(String username, String pwd, String name, Integer permissions) throws InvalidUsernameException {
    	if(username != null && username != "" && StringUtils.isAlphanumeric(username)){
    		Directory rootDir = this.getRootDirectory();
        	User rootUser = this.getRootUser();
        	File home;
        	try {
        		home = rootDir.getDirectory("home");
        	}
        	catch (FileNotFoundException e){
            	this.incrementFileId();
        		home = new Directory("home",getFileId(), new DateTime(), 11111111 /* not sure about this*/, rootUser,rootDir);
        	}
        	this.incrementFileId();
        	Directory userHome = new Directory(username, getFileId(), new DateTime(), 11111111 /* not sure about this*/, rootUser, (Directory) home);
        	User newUser;
        	if(pwd == null || name == null || permissions == null){
        		newUser = new User(username, userHome);
        	}
        	else{
        		newUser = new User(username, pwd, name, permissions, userHome);
        	}
        	userHome.setOwner(newUser);
        	userHome.setOwnerHome(newUser);
        	this.addUsers(newUser);
    	}
    	else{
    		throw new InvalidUsernameException("Username must be not empty and can only have numbers and letters"); 
    	}
    	
    }
    public void incrementFileId(){
    	this.setFileId(getFileId()+1);
    }

	public ArrayList<String> listDir(String path) throws UnsupportedOperationException, FileNotFoundException, NotDirectoryException {
		File file = getFileFromPath(path);
		ListDirVisitor visitor = new ListDirVisitor();
		file.accept(visitor);
		return visitor.getFileNames();
	}
	
	//TODO: Remove
//	public String getFileContents(File file) throws UnsupportedOperationException {
//		if(!(file instanceof PlainFile)){
//			throw new UnsupportedOperationException("Can only get the contents of a plain file");
//		}
//		PlainFile plainFile = (PlainFile) file;
//		return plainFile.getContent();
//	}
//	
//	
//	public String getFileContents(String filePath) throws UnsupportedOperationException, FileNotFoundException, NotDirectoryException {
//		File file = getFileFromPath(filePath);
//		return getFileContents(file);
//	}

	public String getFileContents(File file){
		FileContentsVisitor visitor = new FileContentsVisitor();
		try {
			file.accept(visitor);
		} catch (UnsupportedOperationException e) {
			log.debug("Caught exception while obtaining file contents");
			e.printStackTrace();
		}
		return visitor.getFileContents();
	}
	
	public String getFileContents(String filePath) {
		File file = null;
		try {
			file = getFileFromPath(filePath);
		} catch (FileNotFoundException | NotDirectoryException e) {
			log.debug("Caught exception while obtaining file contents");
			e.printStackTrace();
		}
		return getFileContents(file);
	}
	
	public void deleteFile (String path) throws NotDirectoryException, DirectoryIsNotEmptyException, FileNotFoundException{
		getFileFromPath(path).deleteFile();
	}
	/*
	public void createDirectory(String path, String name) { 
		File directory;
		try {
			 directory = getFileFromPath(path);
		}
		catch (FileNotFoundException e) { 
			log.error("The path requested does not exists"); return;
		}
		catch (NotDirectoryException e) { 
			log.error("The path requested does not exists"); return;
		}
		try {
			if (directory.hasFile(name)) {
				log.error("The file already exists");
				return;
			}
			else {
		    	this.incrementFileId(); 
				Directory newDirectory = new Directory(name, getFileId(), new DateTime(), 11111111, getRootUser(), (Directory) directory);
			}
		} catch (UnsupportedOperationException e){
			log.error("The path requested does not exists");
			return;		
		}
	}*/
}