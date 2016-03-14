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
    rootDirectory = Directory.createRootDirectory("/", getFileId(), new DateTime(), 11111010 , root);
    incrementFileId();

    this.setRootDirectory(rootDirectory);

    Directory homeFolder;

  	try {
  		homeFolder = new Directory("home",getFileId(), new DateTime(), 11111010 , root, rootDirectory);
  		incrementFileId();
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
  		home_root = new Directory("root",getFileId(), new DateTime(), 11111010 , root, homeFolder);
  		incrementFileId();
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
   * Returns path without the leading /
   *
   * @param String
   * @return String
   */
  public static void getSubPath(String path){
    ArrayList<String> pieces = new ArrayList<String>(Arrays.asList(path.split("/")));
    if (pieces.size() > 0 && pieces.get(0).equals(""))
      pieces.remove(0);

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
  public void addFile(String path, File f) throws FileExistsException {
    getRootDirectory().addFile(this.getSubPath(path), f);
  }

  /**
   * Removes a file from a Directory
   *
   * @param String path that includes the file to delete
   */
  public void removeFile(String path) {
    getRootDirectory().removeFile(this.getSubPath(path));
  }

  /**
   * Get a new file id
   *
   * @return int
   *
   */
  public static int getNewFileId(){
    Integer id = FenixFramework.getDomainRoot().getMyDrive().getFileId() + 1;
    FenixFramework.getDomainRoot().getMyDrive().setFileId(id);
  }

  /**
   * Get File from a directory
   */
  public File getFile(String path){
    getRootDirectory().getFile(this.getSubPath(path));
  }

  public Directory getDirectoryFromPath(String path)
    throws FileNotFoundException{
    try{
      File f = getFileFromPath(path);
      f.isParentable();
      return (Directory) f;
    }catch(NotDirectoryException e){
      e.printStackTrace();
      return null;
    }
  }

  public File getFileFromPath(String path) throws FileNotFoundException, NotDirectoryException {
	if(!(path.charAt(0) == '/'))
		throw new FileNotFoundException();
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

  /**
   * Clean database
   */
  public void cleanup(){
	  //for (User user : getUsersSet()) {
		//  user.remove();
	  //}
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

        Directory parent = (Directory) this.getFileFromPath(path);
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
      Directory parent = (Directory) this.getFileFromPath(path);
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
      Directory parent = (Directory) this.getFileFromPath(path);
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
      Directory parent = (Directory) this.getFileFromPath(path);
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
        user.setUsersHome(getDirectoryFromPath(node.getChild("home").getValue()));
    }
    for(Element node : e.getChildren("user")){
      log.trace("Setting user " + node.getAttribute("username").getValue() +
        " home directory: " + node.getChild("home").getValue());
        User user = getUserByUsername(node.getAttribute("username").getValue());
        user.setUsersHome(getDirectoryFromPath(node.getChild("home").getValue()));
    }
  }

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

  public void addUser(String username) throws InvalidUsernameException, UsernameAlreadyInUseException{
	  addUser(username, username, username, 11110000);
  }

  private void addUser(String username, String pwd, String name, Integer permissions)
    throws InvalidUsernameException, UsernameAlreadyInUseException {
    if(username != null && username != "" && StringUtils.isAlphanumeric(username)){

      if(getUserByUsername(username) != null)
        throw new UsernameAlreadyInUseException(username);

      Directory rootDir = this.getRootDirectory();
      User rootUser = this.getRootUser();
      Directory home;
      try {
        home = rootDir.getDirectory("home");
      }
      catch (FileNotFoundException e){
        try {
			home = new Directory("home",getFileId(), new DateTime(), permissions , rootUser,rootDir);
			this.incrementFileId();
		} catch (FileExistsException e1) {
			/* Impossible case */
			log.error("IMPOSSIBLE CASE ABORTING OPERATION");
			return;
		}
      }

      Directory userHome = null;
      User newUser;

      try {
		userHome = new Directory(username, getFileId(), new DateTime(),permissions, rootUser, home);
		this.incrementFileId();

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
    else{
      throw new InvalidUsernameException(
        "Username must be not empty and can only have numbers and letters");
    }
  }



  public ArrayList<String> listDir(String path)
    throws UnsupportedOperationException,
      FileNotFoundException,
      NotDirectoryException {
    File file = getFileFromPath(path);
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
    File file = getFileFromPath(filePath);
    return getFileContents(file);
  }

  public void deleteFile (String path)
    throws NotDirectoryException,
      DirectoryIsNotEmptyException,
      FileNotFoundException{
    getFileFromPath(path).deleteFile();
  }
}
