package pt.tecnico.myDrive.domain;

import java.util.ArrayList;
import java.util.Arrays;

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
    	//FIXME 
    	/*
    	 * Create / folder
    	 * Create root?
    	 
    	 * */
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

    public void addUser(String username, String pwd, String name, Integer permissions){
    	Directory rootDir = this.getRootDirectory();
    	User rootUser = this.getRootUser();
    	this.incrementFileId(); 
    	Directory home = rootDir.getDirectory("home"); 
    	Directory userHome = new Directory(username, getFileId(), new DateTime(), 11111111, rootUser, home); //TODO not sure 11111111

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
    public void incrementFileId(){
    	this.setFileId(getFileId()+1);
    }

	public ArrayList<String> listDir(String path) throws UnsupportedOperationException, FileNotFoundException, NotDirectoryException {
		File file = getFileFromPath(path);
		ListDirVisitor visitor = new ListDirVisitor();
		file.accept(visitor);
		return visitor.getFileNames();
	}
	
	//FIXME
	public String getFileContents(File file) throws UnsupportedOperationException {
		if(!(file instanceof PlainFile)){
			throw new UnsupportedOperationException("Can only get the contents of a plain file");
		}
		PlainFile plainFile = (PlainFile) file;
		return plainFile.getContent();
	}
	
	
	public String getFileContents(String filePath) throws UnsupportedOperationException, FileNotFoundException, NotDirectoryException {
		File file = getFileFromPath(filePath);
		return getFileContents(file);
	}

	
	public void DeleteFile (String path){
		try{
			getFileFromPath(path).deleteFile();
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