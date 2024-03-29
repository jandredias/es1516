package pt.tecnico.myDrive.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidAppContentException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidLinkContentException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.PasswordTooShortException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.PrivateResourceException;
import pt.tecnico.myDrive.exception.TestSetupException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.UserDoesNotExistsException;
import pt.tecnico.myDrive.exception.UsernameAlreadyInUseException;
import pt.tecnico.myDrive.exception.WrongPasswordException;

public class MyDrive extends MyDrive_Base {

	static final Logger log = LogManager.getRootLogger();

	/**
	 * Used for singleton
	 *
	 * @return MyDrive
	 */
	public static MyDrive getInstance() {
		MyDrive md = FenixFramework.getDomainRoot().getMyDrive();

		if (md != null)
			return md;

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
		Guest guest = new Guest();
		try {
			this.addUser(root);
			this.addUser(guest);
		} catch (UsernameAlreadyInUseException e1) {
			// Please it is a new my drive..
			e1.printStackTrace();
		}

		Directory rootDirectory;
		rootDirectory = Directory.createRootDirectory(root);

		this.setRootDirectory(rootDirectory);
		initialSetup();

	}

	private void initialSetup() {
		try {
			Root root = getRootUser();
			User guest = this.getUserByUsername("nobody");
			Directory rootDirectory = this.getRootDirectory();
			
			Directory homeFolder = null;
			homeFolder = new Directory("home", root);
			rootDirectory.addFile("", homeFolder, root);

			Directory home_root = null;
			home_root = new Directory("root", root);
			homeFolder.addFile("", home_root, root);
			root.setUsersHome(home_root);

			Directory home_guest = null;
			home_guest = new Directory("nobody", root);
			homeFolder.addFile("", home_guest, root);
			home_guest.setPermissions(guest.getPermissions());
			home_guest.setOwner(guest);
			guest.setUsersHome(home_guest);
		} catch (FileNotFoundException | FileExistsException | PermissionDeniedException | InvalidFileNameException e) {
			e.printStackTrace();
		}
	}
/*
	public void cleanup() {
		try {
			Root root = getRootUser();

			for (User user : getUsersSet()) {
				try {
					user.delete(root);
				} catch (PrivateResourceException e) {
					// Root can delete everyone that is cleanable
				}
			}
			// Cleaning up every File left
			Directory rootDir = getRootDirectory();
			for (File file : rootDir.getFilesSet()) {
				if (!file.getName().equals("/"))
					file.delete(root);
			}
			initialSetup();
			for (User user : getUsersSet()) {
				System.out.println(user.getUsername());
			}
		} catch (MyDriveException e) {
			e.printStackTrace();
		}
	}
	/* ********************************************************************** */
	/* *************************** Static Methods *************************** */

	/**
	 * Converts a path(string) into an Array
	 */
	public static ArrayList<String> pathToArray(String path) {

		ArrayList<String> pieces = new ArrayList<String>(Arrays.asList(path.split("/")));

		// Removing empty String due to / in first position
		if (pieces.size() > 0 && pieces.get(0).equals(""))
			pieces.remove(0);
		return pieces;
	}

	/**
	 * converts an arraylist to a path (string)
	 */
	public static String arrayToString(ArrayList<String> pieces) {
		String newPath = "";
		for (String s : pieces)
			newPath += (s + "/");
		return newPath;
	}

	/**
	 * Returns path without the leading directory
	 *
	 * @param String
	 * @return String
	 */
	public static String getSubPath(String path) {
		ArrayList<String> pieces = MyDrive.pathToArray(path);
		return MyDrive.arrayToString(pieces);
	}

	/**
	 * Returns path without the filename
	 *
	 * @param String
	 * @return String
	 */
	public static String getPathWithoutFile(String path) {

		ArrayList<String> pieces = MyDrive.pathToArray(path);
		if (pieces.size() > 0 && pieces.get(0).equals(""))
			pieces.remove(pieces.size() - 1);
		return MyDrive.arrayToString(pieces);
	}

	/**
	 * Get a new file id
	 *
	 * @return int
	 *
	 */
	public static int getNewFileId() {
		Integer id = FenixFramework.getDomainRoot().getMyDrive().getFileId();
		FenixFramework.getDomainRoot().getMyDrive().setFileId(id + 1);
		return id;
	}

	/* *************************** Static Methods *************************** */
	/* ********************************************************************** */

	/**
	 * Get File from a directory
	 */
	public File getFile(String path) throws FileNotFoundException {
		/** FIXME Try to delete method **/
		try {
			return getFile(path, getRootUser());
		} catch (PermissionDeniedException e) {
			// Root always have permission, should never happen
			System.err.println("Should Not happen: root get file permission");
			e.printStackTrace();
		}
		return null;
	}

	public File getFile(String path, User user) throws FileNotFoundException, PermissionDeniedException {
		if (path.equals("/"))
			return getRootDirectory();

		return getRootDirectory().getFile(path, user);
	}

	/**
	 * Gets a directory using getFile method FIXME try to delete method;
	 * 
	 * @param String
	 *            path
	 */
	public Directory getDirectory(String path) throws FileNotFoundException, NotDirectoryException {

		try {
			return getDirectory(path, getRootUser());
		} catch (PermissionDeniedException e) {
			// Root always have permission, should never happen
			System.err.println("Should Not happen: root get file permission");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets a directory using getFile method FIXME try to delete method;
	 * 
	 * @param String
	 *            path
	 * @throws PermissionDeniedException
	 */
	public Directory getDirectory(String path, User user) throws FileNotFoundException, PermissionDeniedException {
		File f = getFile(path, user);
		try {
			f.isParentable();
		} catch (NotDirectoryException e) {
			throw new FileNotFoundException("Directory: " + path);
		}
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
	 * Returns Directory on Path, if some Directory does not exists in path.. it
	 * is created with owner as @param user
	 *
	 * @param path
	 * @param user
	 * @return
	 * @throws FileExistsException
	 * @throws InvalidFileNameException
	 * @throws NotDirectoryException
	 * @throws PermissionDeniedException
	 */
	private Directory reallyGetDirectory(String path, User user)
			throws InvalidFileNameException, NotDirectoryException, PermissionDeniedException {

		ArrayList<String> pieces = pathToArray(path);

		Directory nextDir = getRootDirectory();
		while (pieces.size() != 0) {
			try {
				nextDir = getDirectory(pieces.get(0));
			} catch (FileNotFoundException e) {
				Directory newDirectory = null;
				try {
					newDirectory = new Directory(pieces.get(0), user);
					nextDir.addFile("", newDirectory, user);
				} catch (FileExistsException | FileNotFoundException e1) {
					// Will Never Happen
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
	 * @param String
	 *            username
	 * @return User
	 * @throws NoSuchUserException
	 */
	public User getUserByUsername(String username) {
		for (User user : getUsersSet()) {
			if (user.getUsername().equals(username))
				return user;
		}
		return null;
	}

	/**
	 * interface that adds a user to the file system
	 *
	 * @param String
	 *            username
	 * @param String
	 *            password
	 * @param String
	 *            name
	 * @param Int
	 *            mask
	 * @param String
	 *            home
	 *
	 * @return User
	 * @throws InvalidUsernameException
	 * @throws UsernameAlreadyInUseException
	 */
	public void addUser(String username, String password, String name, String mask)
			throws InvalidUsernameException, UsernameAlreadyInUseException {

		User newUser = new User(username, password, name, mask);
		
		Directory usersHome = null;
		try {
			Directory rootHomeDir =	reallyGetDirectory("/home", this.getRootUser()); // Making sure
																// there is a
																// /home
																// directory
			usersHome = reallyGetDirectory("/home/" + username, this.getRootUser());
			usersHome.setOwner(newUser);
			usersHome.setPermissions(newUser.getPermissions());
			usersHome.setDir(rootHomeDir);
		} catch (InvalidFileNameException | NotDirectoryException e) {
			// Should Never Happen ; File had just been Created;
			log.error("CRIT ERROR: File that is not a Direcotry is already created, aborting");
			String message = e.getMessage();
			log.error("Message: " + message);
			assert false;
		} catch (PermissionDeniedException e) {
			log.error("CRIT ERROR: root always have privileges");
			assert false;
		}
		newUser.setUsersHome(usersHome);
		this.addUser(newUser);
	}

	private void addUser(User newUser) throws UsernameAlreadyInUseException {
		User existingUser = getUserByUsername(newUser.getUsername());
		if (existingUser == null) {
			addUsers(newUser);
		} else {
			String name = newUser.getUsername();
			try {
				newUser.delete(getRootUser());
			} catch (PermissionDeniedException e) {
				// root always have permission
			}
			throw new UsernameAlreadyInUseException(name);
		}
	}

	/** FIXME should not be public only curr Dir ! **/
	private String getFileContents(File file) throws UnsupportedOperationException {

		ReadFileContentsVisitor visitor = new ReadFileContentsVisitor(getRootUser());
		try {
			file.accept(visitor);
		} catch (PermissionDeniedException e) {
			// Root always have permission
		}
		return visitor.getFileContents();
	}

	/** FIXME should not be public only curr Dir ! **/
	public String getFileContents(String path)
			throws FileNotFoundException, NotDirectoryException, UnsupportedOperationException {

		File file = getFile(path);
		return getFileContents(file);
	}

	/* ********************************************************************** */
	/* ************************* Add Files Methods ************************** */
	private void addFile(String path, File file, User creator)
			throws FileExistsException, FileNotFoundException, PermissionDeniedException, InvalidFileNameException {

		log.trace("Creating new File: " + file.getName() + " on " + path);
		try {
			getRootDirectory().addFile(path, file, creator);
			String big_path = file.getPath();

			if (big_path.length() > 1024) {

				throw new InvalidFileNameException("Path bigger than 1024");
			}
		} catch (FileExistsException | FileNotFoundException exception) {
			try {
				log.trace("Problems Creating File: " + file.getName());
				file.delete(getRootUser());
			} catch (PermissionDeniedException exc) {
				// Root always have permissions
			}
			throw exception;
		}
		log.trace("File: " + file.getName() + " created with success");
	}

	public void addApplication(String path, String name, User owner, String content) throws FileExistsException,
			InvalidFileNameException, FileNotFoundException, PermissionDeniedException, InvalidAppContentException {

		Application file = new Application(name, owner, content);
		this.addFile(path, file, owner);
	}

	public void addDirectory(String path, String name, User owner)
			throws FileExistsException, InvalidFileNameException, FileNotFoundException, PermissionDeniedException {

		Directory file = new Directory(name, owner);
		this.addFile(path, file, owner);
	}

	public void addLink(String path, String name, User owner, String content) throws FileExistsException,
			InvalidFileNameException, FileNotFoundException, PermissionDeniedException, InvalidLinkContentException {

		Link file = new Link(name, owner, content);
		this.addFile(path, file, owner);
	}

	public void addPlainFile(String path, String name, User owner, String content)
			throws FileExistsException, InvalidFileNameException, FileNotFoundException, PermissionDeniedException {

		PlainFile file = new PlainFile(name, owner, content);
		this.addFile(path, file, owner);
	}

	/* ************************* Add Files Methods ENDS ********************* */
	/* ********************************************************************** */
	/* ---------------------------------------------------------------------- */
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

		List<User> usersSorted = new ArrayList<User>(getUsersSet());
		Collections.sort(usersSorted);

		for (User user : usersSorted) {
			Element userEl = user.xmlExport();
			Attribute usernameEl = userEl.getAttribute("username");
			String username = usernameEl.getValue();
			if (!(username.equals("root") || username.equals("nobody"))) {
				element.addContent(userEl);
			}
		}

		List<Element> filesSorted = new ArrayList<Element>(getRootDirectory().xmlExport());
		Collections.sort(filesSorted, new Comparator<Element>() {
			public int compare(Element e1, Element e2) {
				return Integer.parseInt(e1.getAttribute("id").getValue())
						- Integer.parseInt(e2.getAttribute("id").getValue());
			}
		});
		for (Element el : filesSorted) {
			boolean condition = true;

			if (el.getChild("name").getValue().equals("/")) {
				condition = false;
			}
			if (el.getChild("name").getValue().equals("home") && el.getChild("path").getValue().equals("/")) {
				condition = false;
			}

			if (condition) {
				element.addContent(el);
			}
		}
		return doc;
	}

	/**
	 * Receives the root element of a XML document and imports the file system
	 *
	 * @param Element
	 * @throws ImportDocumentException
	 * @throws InvalidFileNameException
	 * @throws PermissionDeniedException
	 */
	public void xmlImport(Element e) throws ImportDocumentException {

		try {
			for (Element node : e.getChildren()) {

				File file = null;

				if (node.getName().equals("user")) {
					User user;
					// If it's the root user it should not be created
					// but updated Otherwise create the user and set him up
					if (!(node.getAttribute("username").getValue().equals("root")||node.getAttribute("username").getValue().equals("nobody"))) {
						user = new User(node);
						addUser(user);
						// Update users home
						String home = "/home/" + node.getAttribute("username").getValue();
						if (node.getChild("home") != null)
							home = node.getChild("home").getValue();
						user.setUsersHome(reallyGetDirectory(home, getRootUser()));
					} // IGNORE ROOT IMPORT!

				} else {
					if (node.getName().equals("dir")) {
						file = new Directory(node);
					} else if (node.getName().equals("plain")) {
						file = new PlainFile(node);
					} else if (node.getName().equals("app")) {
						file = new Application(node);
					} else if (node.getName().equals("link")) {
						file = new Link(node);
					}

					// Set files owner
					User owner = getUserByUsername((node.getChild("owner") == null) ?
							"root" :
								node.getChild("owner").getValue());
					file.setOwner(owner);

					// Set file's parent
					Directory dir = reallyGetDirectory(node.getChild("path").getValue(), this.getRootUser());
					dir.addFiles(file);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ImportDocumentException(e1.getClass() + " : " + e1.getMessage());
		}
	}
	/* ****************************** XML Related *************************** */
	/* ********************************************************************** */

	/* ********************************************************************** */
	/* **************************** Tokens Related ************************** */
	/**
	 * gets a session by token
	 *
	 * @param long
	 *            token
	 * @return Session
	 */
	public Session getSessionByToken(long token) {
		for (Session session : getDriveSessions())
			if (session.getToken().equals(token))
				return session;
		return null;
	}

	public Long getNewToken() {
		while (true) {
			Long token = ThreadLocalRandom.current().nextLong();
			if (token == 0)
				continue;
			for (Session session : getDriveSessions())
				if (session.getToken().equals(token))
					continue;
			return token;
		}
	}

	/**
	 * return the corresponding Session if the token is valid
	 * 
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 */
	public Session validateToken(long token) throws InvalidTokenException {
		Session session = getSessionByToken(token);
		if (session != null) {
			if (session.validateSession()) {
				return session;
			}
		}
		log.warn("Non Active Token was used");
		throw new InvalidTokenException();
	}

	public long getValidToken(String username, String currentDirectoryPath, StrictlyTestObject testsOnly) {
		if (testsOnly != null) {
			User user = getUserByUsername(username);
			if (user == null)
				throw new TestSetupException("GetValidSessionError: Invalid user: " + username);

			Directory directory = null;
			try {
				directory = getDirectory(currentDirectoryPath);
			} catch (FileNotFoundException | NotDirectoryException e) {
				throw new TestSetupException("GetValidSessionError: Invalid directory: " + currentDirectoryPath);
			}

			long token = this.getNewToken();
			Session session = new Session(user, token);
			session.setCurrentDirectory(directory);
			return token;
		} else {
			throw new TestSetupException("Only Tests Allowed");
		}
	}

	@Override
	public java.util.Set<Session> getSessionSet() throws PrivateResourceException {
		log.warn("Atempting to access session Set");
		throw new PrivateResourceException("Session Set is private");
	}

	private java.util.Set<Session> getDriveSessions() {
		return super.getSessionSet();
	}

	/**
	 * This method cleans old sessions of any user in the system
	 */
	public void cleanSessions() {
		for (Session s : getDriveSessions())
			if (!s.valid())
				removeSession(s);
	}

	public Long login(String username, String password)
			throws UserDoesNotExistsException, WrongPasswordException, PasswordTooShortException {

		cleanSessions();

		if (password == null)
			throw new WrongPasswordException();

		User user = this.getUserByUsername(username);
		if (user == null)
			throw new UserDoesNotExistsException();

		boolean specialUser = user.specialPassUser();
		if (password.length() < 8 && !specialUser)
			throw new PasswordTooShortException();

		// if(!user.getPassword().equals(password)) throw new
		// WrongPasswordException();
		if (user.wrongPassword(password))
			throw new WrongPasswordException();

		Session s = new Session(user, this.getNewToken());
		s.setCurrentDirectory(user.getUsersHome());

		return s.getToken();

	}

	public void eraseSession(long token) throws InvalidTokenException {
		Session session = this.getSessionByToken(token);
		if (session != null) {
			removeSession(session);
		} else {
			throw new InvalidTokenException("token does not exists");
		}
	}

	/* **************************** Tokens Related ************************** */
	/* ********************************************************************** */

}
