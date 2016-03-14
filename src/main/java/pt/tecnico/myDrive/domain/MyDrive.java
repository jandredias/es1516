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
import pt.tecnico.myDrive.exception.FileAlreadyExistsException;

public class MyDrive extends MyDrive_Base {

  static final Logger log = LogManager.getRootLogger();

  public static MyDrive getInstance() {
    MyDrive md = FenixFramework.getDomainRoot().getMyDrive();
    if (md != null) return md;
    md = new MyDrive();
    log.trace("new MyDrive");

    return md;
  }

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
	} catch (FileAlreadyExistsException e) {
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
  	} catch (FileAlreadyExistsException e) {
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
    NotDirectoryException, NoSuchUserException, FileAlreadyExistsException {
    Element root = e.getChild("root");
    if(root != null){
      User rootUser = getRootUser();
      log.trace("Adding root to filesystem");
      rootUser.setName(root.getChild("name").getValue());
      rootUser.setUsername(root.getAttribute("username").getValue());
      rootUser.setPassword(root.getChild("password").getValue());
      rootUser.setPermissions(Integer.parseInt(root.getChild("permissions").getValue()));
    }
    for(Element node : e.getChildren("user")){
      log.trace("Adding user to filesystem: " + node.getAttribute("username").getValue());
      try{
        this.addUser(
          node.getAttribute("username").getValue(),
          node.getChild("password").getValue(),
          node.getChild("name").getValue(),
          Integer.parseInt(node.getChild("permissions").getValue())
        );
      }catch(UsernameAlreadyInUseException ex){ //Update user
        User user = getUserByUsername(node.getAttribute("username").getValue());
        user.setName(node.getChild("name").getValue());
        user.setPassword(node.getChild("password").getValue());
        user.setPermissions(Integer.parseInt(node.getChild("permissions").getValue()));
        user.setUsersHome(getDirectoryFromPath(node.getChild("home").getValue()));
      }
    }

    //Import Directories
    for(Element dir : e.getChildren("directory")){
        Directory parent = (Directory) this.getFileFromPath(dir.getChild("path").getValue());
        User owner = getUserByUsername(dir.getChild("owner").getValue());
        try{
          parent.getFile(dir.getAttribute("name").getValue());
        }catch(FileNotFoundException es){
          new Directory(dir, owner, parent);
        }
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
		} catch (FileAlreadyExistsException e1) {
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

      } catch (FileAlreadyExistsException e) {
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
      userHome.setOwnerHome(newUser);
      this.addUsers(newUser);
    }
    else{
      throw new InvalidUsernameException(
        "Username must be not empty and can only have numbers and letters");
    }
  }

  public void incrementFileId(){
    this.setFileId(getFileId()+1);
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
