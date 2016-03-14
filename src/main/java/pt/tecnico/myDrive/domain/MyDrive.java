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

    Directory homeFolder;

	try {
		homeFolder = new Directory("home", new DateTime(), 11111010 , root, rootDirectory);
		getNewFileId();
	} catch (FileExistsException e) {
		try {
			homeFolder = rootDirectory.getDirectory("home");
		} catch (FileNotFoundException e1) {
			/* Impossible case */
			log.error("IMPOSSIBLE CASE ABORTING OPERATION");
			return;
		}
	}

    Directory home_root;
  	try {
  		home_root = new Directory("root", new DateTime(), 11111010 , root, homeFolder);
  		getNewFileId();
  	} catch (FileExistsException e) {
  		try {
  			home_root = rootDirectory.getDirectory("root");
  		} catch (FileNotFoundException e1) {
  			/* Impossible case */
  			log.error("IMPOSSIBLE CASE ABORTING OPERATION");
  			return;
  		}
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
   * Add file to Directory
   *
   * @param String path that doesn't not include the filename
   * @param File file to be added
   */
  public void addFile(String path, File f)
  throws FileExistsException, FileNotFoundException {
    getRootDirectory().addFile(MyDrive.getSubPath(path), f);
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
   * Searchs the root user and returns it
   *
   * @return Root
   */
  public Root getRootUser() {
    return (Root) getRootDirectory().getOwner();
  }

  /**
   * Clean database
   */
  public void cleanup(){
    //TODO
    //Remove users
    //Remove directories
  }

  /**
   * Receives the root element of a XML document and imports the file system
   *
   * @param Element
   */
  public void xmlImport(Element e)
    throws InvalidUsernameException, FileNotFoundException,
    NotDirectoryException, NoSuchUserException, FileExistsException {
    Element root = e.getChild("root");
    if(root != null){
      User rootUser = getRootUser();
      log.trace("Adding root to filesystem");
      rootUser.setName(root.getChild("name").getValue());
      rootUser.setUsername(root.getAttribute("username").getValue());
      rootUser.setPassword(root.getChild("password").getValue());
      rootUser.setPermissions(Integer.parseInt(root.getChild("mask").getValue()));
    }
    for(Element node : e.getChildren("user")){
      log.trace("Adding user to filesystem: " + node.getAttribute("username").getValue());
      try{
        this.addUser(
          node.getAttribute("username").getValue(),
          node.getChild("password").getValue(),
          node.getChild("name").getValue(),
          Integer.parseInt(node.getChild("mask").getValue())
        );
      }catch(UsernameAlreadyInUseException ex){ //Update user
        User user = getUserByUsername(node.getAttribute("username").getValue());
        user.setName(node.getChild("name").getValue());
        user.setPassword(node.getChild("password").getValue());
        user.setPermissions(Integer.parseInt(node.getChild("mask").getValue()));
      }
    }

    //Import Directories
    for(Element dir : e.getChildren("dir")){

      String name = dir.getChild("name").getValue();
      String path = dir.getChild("path").getValue();
      String ownerUsername = dir.getChild("owner").getValue();

      log.trace("Importing directory " + name + " on " + path);

        Directory parent = (Directory) this.getFile(path);
        User owner = getUserByUsername(ownerUsername);
        try{
          parent.getFile(name);
        }catch(FileNotFoundException es){
          new Directory(dir, owner, parent);
        }
    }

    for(Element plain : e.getChildren("plain")){
      String name = plain.getChild("name").getValue();
      String path = plain.getChild("path").getValue();
      String ownerUsername = plain.getChild("owner").getValue();
      log.trace("Importing plain-file " + name + " on " + path);
      Directory parent = (Directory) this.getFile(path);
      User owner = getUserByUsername(ownerUsername);
      try{
        parent.getFile(name);
      }catch(FileNotFoundException es){
        new PlainFile(plain, owner, parent);
      }
    }
    for(Element link : e.getChildren("link")){
      String name = link.getChild("name").getValue();
      String path = link.getChild("path").getValue();
      String ownerUsername = link.getChild("owner").getValue();
      log.trace("Importing link " + name + " on " + path);
      Directory parent = (Directory) this.getFile(path);
      User owner = getUserByUsername(ownerUsername);
      try{
        parent.getFile(name);
      }catch(FileNotFoundException es){
        new Link(link, owner, parent);
      }
    }
    for(Element app : e.getChildren("app")){
      String name = app.getChild("name").getValue();
      String path = app.getChild("path").getValue();
      String ownerUsername = app.getChild("owner").getValue();
      log.trace("Importing app " + name + " on " + path);
      Directory parent = (Directory) this.getFile(path);
      User owner = getUserByUsername(ownerUsername);
      try{
        parent.getFile(name);
      }catch(FileNotFoundException es){
        new Application(app, owner, parent);
      }
    }
    //Need to repeat this for setting users home correctly
    for(Element node : e.getChildren("root")){
      log.trace("Setting user " + node.getAttribute("username").getValue() +
        " home directory: " + node.getChild("home").getValue());
        User user = getUserByUsername(node.getAttribute("username").getValue());
        user.setUsersHome(getDirectory(node.getChild("home").getValue()));
    }
    for(Element node : e.getChildren("user")){
      log.trace("Setting user " + node.getAttribute("username").getValue() +
        " home directory: " + node.getChild("home").getValue());
        User user = getUserByUsername(node.getAttribute("username").getValue());
        user.setUsersHome(getDirectory(node.getChild("home").getValue()));
    }
  }

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
  Integer mask, String home) throws NotDirectoryException, InvalidUsernameException,
  UsernameAlreadyInUseException{

    ArrayList<String> pieces = new ArrayList<String>(Arrays.asList(home.split("/")));
    String userHomeName = pieces.get(pieces.size()-1);

    Directory rootHome;
    Directory usersHome;
    try{
        rootHome = getDirectory(MyDrive.getPathWithoutFile(home));
        try{
          usersHome = getDirectory(home);
        }catch(FileNotFoundException e){
          addFile(MyDrive.getPathWithoutFile(home),
              new Directory(userHomeName, new DateTime(), 11111010, getRootUser(), rootHome));
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

  public void addUser(String username)
    throws InvalidUsernameException, UsernameAlreadyInUseException,
    NotDirectoryException{
	  addUser(username, username, username, 11110000, "/home/" + username);
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
			home = new Directory("home", new DateTime(), permissions , rootUser,rootDir);

			MyDrive.getNewFileId();
		} catch (FileExistsException e1) {
			/* Impossible case */
			log.error("IMPOSSIBLE CASE ABORTING OPERATION");
			return;
		}
      }

      Directory userHome = null;
      User newUser;

      try {
		userHome = new Directory(username, new DateTime(),permissions, rootUser, home);

		MyDrive.getNewFileId();

      } catch (FileExistsException e) {
    	try {
			userHome = home.getDirectory(username);
		} catch (FileNotFoundException e1) {
			/* Impossible case */
			log.error("IMPOSSIBLE CASE ABORTING OPERATION");
			return;
		}
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
    throws UnsupportedOperationException,
      FileNotFoundException,
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
  public String getFileContents(String filePath)
    throws FileNotFoundException,
    NotDirectoryException, UnsupportedOperationException {
    File file = getFile(filePath);
    return getFileContents(file);
  }

  public void deleteFile (String path)
    throws NotDirectoryException,
      DirectoryIsNotEmptyException,
      FileNotFoundException{
    getFile(path).deleteFile();
  }
}
