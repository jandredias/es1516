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

public class MyDrive extends MyDrive_Base {

  static final Logger log = LogManager.getRootLogger();

  public static MyDrive getInstance() {
    MyDrive md = FenixFramework.getDomainRoot().getMyDrive();
    if (md != null) return md;

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
    //TODO
  }

  /**
   * Receives the root element of a XML document and imports the filesystem
   *
   * @param Element
   */
  public void xmlImport(Element e)
    throws InvalidUsernameException, FileNotFoundException,
    NotDirectoryException, NoSuchUserException {
    Element root = e.getChild("root");
    if(root != null){
      log.trace("Adding root to filesystem");
      this.getRootUser().setName(root.getAttribute("name").getValue());
      this.getRootUser().setUsername(root.getAttribute("username").getValue());
      this.getRootUser().setPassword(root.getAttribute("password").getValue());
      this.getRootUser().setPermissions(Integer.parseInt(root.getAttribute("permissions").getValue()));
    }
    for(Element node : e.getChildren("user")){
      log.trace("Adding user to filesystem: " + node.getAttribute("username").getValue());
      try{
        this.addUser(
          node.getAttribute("username").getValue(),
          node.getAttribute("password").getValue(),
          node.getAttribute("name").getValue(),
          Integer.parseInt(node.getAttribute("permissions").getValue())
        );
      }catch(UsernameAlreadyInUseException ex){ //Update user
        User user = getUserByUsername(node.getAttribute("username").getValue());
        user.setName(node.getAttribute("name").getValue());
        user.setPassword(node.getAttribute("password").getValue());
        user.setPermissions(Integer.parseInt(node.getAttribute("permissions").getValue()));
        user.setUsersHome(getDirectoryFromPath(node.getAttribute("home").getValue()));
      }
    }

    //TODO
    //Import Directories
    for(Element dir : e.getChildren("directory")){
        Directory parent = (Directory) this.getFileFromPath(dir.getAttribute("parent").getValue());
        User owner = null;//FIXME
        parent.addFiles(new Directory(dir, parent, owner));
    }
  }

  public Document xmlExport() {
    Element element = new Element("mydrive");
    Document doc = new Document(element);

    for(User user: getUsersSet())
      element.addContent(user.xmlExport());

    //Directory rootDirectory = getRootDirectory();
    //element.addContent(rootDirectory.xmlExport());

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

  public void addUser(String username, String pwd, String name, Integer permissions)
    throws InvalidUsernameException, UsernameAlreadyInUseException {
    if(username != null && username != "" && StringUtils.isAlphanumeric(username)){

      if(getUserByUsername(username) != null)
        throw new UsernameAlreadyInUseException(username);

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

  public String getFileContents(String filePath)
    throws FileNotFoundException,
    NotDirectoryException {
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
