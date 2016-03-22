package pt.tecnico.myDrive.domain;

import java.util.ArrayList;
import java.util.Arrays;

import java.lang.reflect.*;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.NoSuchUserException;
import pt.tecnico.myDrive.exception.UsernameAlreadyInUseException;
import pt.tecnico.myDrive.exception.FileExistsException;

public class MyDrive extends MyDrive_Base {

	static final Logger log = LogManager.getRootLogger();

	/**
	 * Used for singleton
	 *
	 * @return MyDrive
	 */
	public static MyDrive getInstance() {

		MyDrive md = FenixFramework.getDomainRoot().getMyDrive();

		if (md != null) return md;

		md = new MyDrive();
		log.trace("new MyDrive");

		return md;
	}

	/**
	 * Constructor
	 */
	private MyDrive() {

		setRoot(FenixFramework.getDomainRoot());

		this.setFileId(0);

		Root root = new Root();
		this.addUsers(root);

		Directory rootDirectory;
		rootDirectory = Directory.createRootDirectory(root);
		getNewFileId();

		this.setRootDirectory(rootDirectory);

		Directory homeFolder = null;
		try {
			homeFolder = new Directory("home",root);
			rootDirectory.addFile("", homeFolder);
		} catch (FileExistsException e) {
			try {
				homeFolder = rootDirectory.getDirectory("home");
			} catch (FileNotFoundException exc) {
				//Should Never Happen
				log.error("CRIT ERROR: root dir should exist");
				log.error("Message: " + exc.getMessage());
				assert false;
			}
		} catch (InvalidFileNameException | FileNotFoundException exc) {
			//Should Never Happen ; 
			log.error("CRIT ERROR: root dir should exist");
			log.error("Message: " + exc.getMessage());
			assert false;		
		}


		Directory home_root = null;
		try {
			home_root = new Directory("root", root);
			homeFolder.addFile("", home_root );
		} catch (FileExistsException e) {
			try {
				home_root = rootDirectory.getDirectory("root");
			} catch (FileNotFoundException exc) {
				//Should Never Happen
				log.error("CRIT ERROR: root dir should exist");
				log.error("Message: " + exc.getMessage());
				assert false;
			}
		} catch (InvalidFileNameException | FileNotFoundException exc) {
			//Should Never Happen ; 
			log.error("CRIT ERROR: root dir should exist");
			log.error("Message: " + exc.getMessage());
			assert false;		
		}
		root.setUsersHome(home_root);
	}

	/**
	 * Returns path without the leading directory
	 *
	 * @param String
	 * @return String
	 */
	public static String getSubPath(String path){
		ArrayList<String> pieces = new ArrayList<String>(Arrays.asList(path.split("/")));
		if (pieces.size() > 0 && pieces.get(0).equals(""))
			pieces.remove(0);

		String newPath = "";
		for(String s : pieces)
			newPath += (s + "/");
		return newPath;
	}

	/**
	 * Returns path without the filename
	 *
	 * @param String
	 * @return String
	 */
	public static String getPathWithoutFile(String path){
		ArrayList<String> pieces = new ArrayList<String>(Arrays.asList(path.split("/")));
		if (pieces.size() > 0 && pieces.get(0).equals(""))
			pieces.remove(pieces.size() - 1);

		String newPath = "";
		for(String s : pieces)
			newPath += (s + "/");
		return newPath;
	}


	/**
	 * Removes a file from a Directory
	 *
	 * @param String path that includes the file to delete
	 * @throws DirectoryIsNotEmptyException
	 */
	public void removeFile(String path)
		throws FileNotFoundException, DirectoryIsNotEmptyException{
			getRootDirectory().removeFile(MyDrive.getSubPath(path));
		}

	/**
	 * Get a new file id
	 *
	 * @return int
	 *
	 */
	public static int getNewFileId(){
		Integer id = FenixFramework.getDomainRoot().getMyDrive().getFileId();
		FenixFramework.getDomainRoot().getMyDrive().setFileId(id + 1);
		return id;
	}

	/**
	 * Get File from a directory
	 */
	public File getFile(String path) throws FileNotFoundException {
		if (path.equals("/"))
			return getRootDirectory();
		
		return getRootDirectory().getFile(MyDrive.getSubPath(path));
	}

	/**
	 * Gets a directory using getFile method
	 *
	 * @param String path
	 */
	public Directory getDirectory(String path)
		throws FileNotFoundException, NotDirectoryException {
			File f = getFile(path);
			f.isParentable();
			return (Directory) f;
	}

	/**
	 * Search the root user and returns it
	 *
	 * @return Root
	 */
	public Root getRootUser() {
		return (Root) getRootDirectory().getOwner();
	}

	/**
	 * Returns File on Path, if some Directory does not exists it is created
	 * @param path
	 * @return
	 * @throws FileExistsException
	 * @throws InvalidFileNameException
	 * @throws NotDirectoryException
	 */
	private Directory reallyGetFile(String path)throws FileExistsException, 
			InvalidFileNameException, NotDirectoryException{

		ArrayList<String> pieces = new ArrayList<String>(Arrays.asList(path.split("/")));
		
		//Removing empty String due to / in first position
		if (pieces.size() > 0 && pieces.get(0).equals(""))
			pieces.remove(0);

		Directory nextDir = getRootDirectory();
		while ( pieces.size() != 0 ) {
			try {
				nextDir = getDirectory(pieces.get(0));
			} catch (FileNotFoundException e) {
				User root = this.getRootUser();
				Directory newDirectory = new Directory(pieces.get(0), root);
				try {
					nextDir.addFile("", newDirectory);
				} catch (FileNotFoundException e1) {
					//Will Never Happen
				}
				nextDir = newDirectory;
			} 
			pieces.remove(0);
		}
		return nextDir;
	}


	/**
	 * gets a user by username
	 *
	 * @param String username
	 * @return User
	 * @throws NoSuchUserException
	 */
	public User getUserByUsername(String username) {
		for(User user : getUsersSet())
			if(user.getUsername().equals(username)) return user;
		return null;
	}

	/**
	 * Adds a user to the file system
	 *
	 * @param String username
	 * @param String password
	 * @param String name
	 * @param Int mask
	 * @param String home
	 *
	 * @return User
	 */
	public void addUser(String username, String password, String name,
			Integer mask, String home) throws NotDirectoryException, 
			InvalidUsernameException, UsernameAlreadyInUseException{

		ArrayList<String> pieces = new ArrayList<String>(Arrays.asList(home.split("/")));
		String userHomeName = pieces.get(pieces.size()-1);

		Directory rootHome;
		Directory usersHome;
		try{
			rootHome = getDirectory(MyDrive.getPathWithoutFile(home));
			try{
				usersHome = getDirectory(home);
			}catch(FileNotFoundException e){
				try {
					Directory d = new Directory(userHomeName, getRootUser());
					rootHome.addFile("", d);
				} catch (InvalidFileNameException e1) {
					throw new InvalidUsernameException();
				}
				usersHome = getDirectory(home);
			}
			this.addUser(username, password, name, mask, usersHome);
		}catch(FileNotFoundException | FileExistsException e){
			//won't happen
		}

	}

	public void addUser(String username, String password, String name, Integer mask,
			Directory home) throws InvalidUsernameException, UsernameAlreadyInUseException {

		if(username == null || username == "" || !StringUtils.isAlphanumeric(username))
			throw new InvalidUsernameException(
					"Username must be not empty and can only have numbers and letters");

		if(getUserByUsername(username) != null)
			throw new UsernameAlreadyInUseException(username);

		User user = new User(username, password, name, mask, home);
		home.setOwner(user);
		home.addOwnerHome(user);
		this.addUsers(user);
	}

	public void addUser(String username) throws InvalidUsernameException, 
			UsernameAlreadyInUseException, NotDirectoryException{

		addUser(username, username, username, 11110000, "/home/" + username);
	}


	public void addUser(User user) throws UsernameAlreadyInUseException {
		User newUser = getUserByUsername(user.getUsername());
		if( newUser == null ) {
			addUsers(user);
		} else {
			throw new UsernameAlreadyInUseException(user.getUsername());
		}
	}
	
	private void addUser(String username, String pwd, String name, Integer permissions)
		throws InvalidUsernameException, UsernameAlreadyInUseException {

			Directory rootDir = this.getRootDirectory();
			User rootUser = this.getRootUser();
			Directory home;
			try {
				home = rootDir.getDirectory("home");
			}
			catch (FileNotFoundException e){
				try {
					home = new Directory("home", rootUser);
					rootDir.addFile("", home);
				} catch (FileNotFoundException | FileExistsException |
						InvalidFileNameException e1) {
					
					/* Impossible case */
					log.error("IMPOSSIBLE CASE ABORTING OPERATION");
					return;
				}
			}

			Directory userHome = null;
			User newUser;

			try {
				userHome = new Directory(username, rootUser);
				home.addFile("", userHome);

			} catch (FileExistsException e) {
				try {
					userHome = home.getDirectory(username);
				} catch (FileNotFoundException e1) {
					/* Impossible case */
					log.error("IMPOSSIBLE CASE ABORTING OPERATION");
					return;
				}
			} catch (InvalidFileNameException e) {
				throw new InvalidUsernameException(username);
			} catch (FileNotFoundException e) {
				//Wont Happen
			} finally {
				if(pwd == null || name == null || permissions == null){
					newUser = new User(username, userHome);
				}
				else{
					newUser = new User(username, pwd, name, permissions, userHome);
				}
			}

			userHome.setOwner(newUser);
			userHome.addOwnerHome(newUser);
			this.addUsers(newUser);
	}



	public ArrayList<String> listDir(String path)
			throws UnsupportedOperationException, FileNotFoundException,
			NotDirectoryException {

		File file = getFile(path);
		ListDirVisitor visitor = new ListDirVisitor();
		file.accept(visitor);
		return visitor.getFileNames();
	}

	private String getFileContents(File file) throws UnsupportedOperationException{
		FileContentsVisitor visitor = new FileContentsVisitor();
		file.accept(visitor);
		return visitor.getFileContents();
	}
	
	public String getFileContents(String path)
			throws FileNotFoundException,
			NotDirectoryException, UnsupportedOperationException {
		File file = getFile(path);
		return getFileContents(file);
	}

	public void deleteFile (String path) throws FileNotFoundException, 
			DirectoryIsNotEmptyException {

		this.getRootDirectory().removeFile(path);
	}

	public static String permissions(int p){
		//TODO
		return "rwxdr-x-";
	}
	public static int permissions(String p){
		//TODO
		return 11111010;
	}

	/* ********************************************************************** */
	/* ********************************************************************** */
	/* ************************* Add Files Methods ************************** */
	private void addFile(String path, File file) throws FileExistsException ,
			FileNotFoundException {

		log.trace("Creating new File: " + file.getName() + " on " + path);
		try {
			getRootDirectory().addFile(path, file);
		} catch (FileExistsException | FileNotFoundException exception) {
			try {
				log.trace("Problems Creating File: " + file.getName());
				file.delete();
			} catch (DirectoryIsNotEmptyException exc) {
				//Should Never Happen ; File had just been Created;
				log.error("CRIT ERROR: Just Created File, NotEmptyException");
				String message = exc.getMessage();
				log.error("Message: " + message);
				assert false;
			} 
			throw exception;
		}
		log.trace("File: " + file.getName() + " created with success");
	}
	
	public void addApplication(String path, String name, User owner, 
			String content)throws FileExistsException, InvalidFileNameException, 
			FileNotFoundException {

		Application file = new Application(name, owner, content);
		this.addFile(path, file);
	}
	
	public void addDirectory(String path, String name, User owner) 
			throws FileExistsException, InvalidFileNameException, 
			FileNotFoundException {

		Directory file = new Directory(name, owner);
		this.addFile(path, file);
	}
	
	public void addLink(String path, String name, User owner, 
			String content)throws FileExistsException, InvalidFileNameException, 
	FileNotFoundException {
		
		Link file = new Link(name, owner, content);
		this.addFile(path, file);
	}
	
	public void addPlainFile(String path, String name, User owner, 
			String content)throws FileExistsException, InvalidFileNameException, 
	FileNotFoundException {
		
		PlainFile file = new PlainFile(name, owner, content);
		this.addFile(path, file);
	}

	/* ************************* Add Files Methods ENDS ********************* */
	/* ********************************************************************** */
	/* ********************************************************************** */
	/* ---------------------------------------------------------------------- */
	/* ********************************************************************** */
	/* ********************************************************************** */
	/* ****************************** XML Related *************************** */

	/**
	 * Exports the file system to a XML Document
	 *
	 * @return Document
	 */
	public Document xmlExport() {
		Element element = new Element("mydrive");
		Document doc = new Document(element);

		for(User user: getUsersSet())
			element.addContent(user.xmlExport());

		for(Element el : getRootDirectory().xmlExport())
			element.addContent(el);

		return doc;
	}
	
	/**
	 *  
	 *
	 * @param Object 
	 */
	private void importElement(Element e, Object j){
		System.out.println("This should happen");
	}

	/**
	 * Import element: adds it to its parent
	 *
	 * @param File
	 */
	private void importElement(Element e, File f){
		//TODO
		System.out.println("FILE HERE");
		//f.setOwner(this.getUser(e.getChild("owner").getValue()));
	}

	/**
	 * Import element: adds it to its parent
	 *
	 * @param User
	 */
	private void importElement(Element e, User u){
		//TODO
		System.out.println("User HERE");
		
	}

	/**
	 * Receives the root element of a XML document and imports the file system
	 *
	 * @param Element
	 * @throws InvalidFileNameException
	 */
	public void xmlImport(Element e)
			throws InvalidUsernameException, FileNotFoundException,
			NotDirectoryException, NoSuchUserException,
			FileExistsException, InvalidFileNameException,
			ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		
		for(Element node : e.getChildren()){
			File file = null ;
			if(node.getName().equals("user")){
				User user = new User(node);
				try{
					addUser(user);
				} catch(UsernameAlreadyInUseException u) {
					user = getUserByUsername(node.getAttribute("username").getValue());
					user.xmlImport(node); //Update information
				}
			} else if(node.getName().equals("dir")) {
				file = new Directory(node);
			} else if(node.getName().equals("plain-file")) {
				file = new PlainFile(node);
			} else if(node.getName().equals("app")) {
				file = new Application(node);
			} else if(node.getName().equals("link")) {
				file = new Link(node);
			}
			
			if(! node.getName().equals("user")) {
				reallyGetFile(node.getChild("path").getValue()).addFile("",file);
			}
		}
	}
	/* ****************************** XML Related *************************** */
	/* ********************************************************************** */
	/* ********************************************************************** */
}
