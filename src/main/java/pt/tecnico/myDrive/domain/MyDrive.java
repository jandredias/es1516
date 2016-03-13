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
    	
    	this.setFileId(0);
    	
    	Root root = new Root();
    	this.addUsers(root);
    	
    	Directory rootDirectory = new Directory("/", getFileId(), new DateTime(), 11111010 , root, null);
    	rootDirectory.setDir(rootDirectory);
    	this.setRootDirectory(rootDirectory);
    	
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
    	if (pieces.size() > 0 && pieces.get(0).equals(""))
            pieces.remove(0);
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

	public String getFileContents(File file) throws UnsupportedOperationException{
		FileContentsVisitor visitor = new FileContentsVisitor();
		file.accept(visitor);
		return visitor.getFileContents();
	}
	
	public String getFileContents(String filePath) throws FileNotFoundException, NotDirectoryException, UnsupportedOperationException {
		File file = getFileFromPath(filePath);
		return getFileContents(file);
	}
	
	public void deleteFile (String path) throws NotDirectoryException, DirectoryIsNotEmptyException, FileNotFoundException{
		getFileFromPath(path).deleteFile();
	}
}