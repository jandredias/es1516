package pt.tecnico.myDrive.domain;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Element;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.PasswordTooShortException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

public class User extends User_Base implements Comparable<User> {

	private static final String DEFAULT_PERMISSION = "rwxd----";

	protected User() {/*for subclasses to use*/}

	protected User(String username, String pwd, String name, String permissions) throws InvalidUsernameException {
		init(username, pwd, name, permissions);
	}

	protected User(String username) throws InvalidUsernameException{
		init(username, username, username, DEFAULT_PERMISSION);
	}

	protected void init(String username, String pwd, String name,
			String permissions) throws InvalidUsernameException{

		if(username == null || username == "" || !StringUtils.isAlphanumeric(username)){
			this.deleteDomainObject();
			throw new InvalidUsernameException(
					"Username must be not empty and can only have numbers and letters");
		}

		if(pwd == null)
			pwd = username;

		if(name == null)
			name = username;

		if(permissions == null)
			permissions = DEFAULT_PERMISSION;

		setUsername(username);
		
		if(username.equals("root")){
			super.setPassword(pwd);
		}
		else {
			this.setPassword(pwd);
		}
		setName(name);
		setPermissions(permissions);
	}
	
	@Override
	public void setUsername(String username) throws InvalidUsernameException{
		if(username.length() < 3)
			throw new InvalidUsernameException();
		else
			super.setUsername(username);
	}
	
	@Override
	public void setPassword(String newPass) throws PasswordTooShortException{
		if(newPass.length() < 8)
			throw new PasswordTooShortException("Desired Pass: " + newPass);
		else
			super.setPassword(newPass);
	}

	/**
	 * Method only to be used by Guest 
	 * @param guestPass
	 * @throws PasswordTooShortException
	 */
	protected void setGuestPassword(String guestPass){
		super.setPassword(guestPass);
	}

	public User(Element xml) {
		this.xmlImport(xml);
	}

	protected void xmlImport(Element xml) {
		String username = xml.getAttribute("username").getValue();
		setUsername(username);

		setName((xml.getChild("name") == null) ?
				this.getUsername() :
					xml.getChild("name").getValue());

		setPassword((xml.getChild("password") == null) ?
				this.getUsername() :
					xml.getChild("password").getValue());

		setPermissions((xml.getChild("mask") == null) ?
			DEFAULT_PERMISSION :
			xml.getChild("mask").getValue());
	}

	public Element xmlExport() {
		Element element = new Element("user");
		element.setAttribute("username", getUsername());

		Element passwordElement = new Element("password");
		passwordElement.addContent(getPassword());

		Element nameElement = new Element("name");
		nameElement.addContent(getName());

		Element permissionsElement = new Element("mask");
		permissionsElement.addContent(getPermissions());

		Element homeElement = new Element("home");
		homeElement.addContent(getUsersHome().getPath());

		element.addContent(passwordElement);
		element.addContent(nameElement);
		element.addContent(permissionsElement);
		element.addContent(homeElement);


		return element;
	}

	protected void delete(User deleter) throws PermissionDeniedException{
		//*********************************
		//*********************************
		for (File file : getOwnedFilesSet()){
			file.delete(deleter);
		}
		setUsersHome(null);
		this.setMyDrive(null);
		//*********************************
		//*********************************
		this.deleteDomainObject();
	}

	public int compareTo(User u){
		return this.getUsername().compareTo(u.getUsername());
	}

	/**
	 * METHOD ONLY USED BY USERS AND SUBCLASSES!!
	 * General method that states whether or not a user has permissions to access a file
	 * @param file
	 * @param position
	 * @param permissionLetter
	 * @return
	 */
	protected boolean hasPermissions(File file, int position, String permissionLetter){
		User owner = file.getOwner();
		String filePermissions = file.getPermissions();
		if(filePermissions.length() == 8){
			if(owner.equals(this)){
				return filePermissions.substring(position, position+1).equals(permissionLetter);
			}
			else{
				return filePermissions.substring(position+4, position+5).equals(permissionLetter);
			}
		}
		return false;
	}

	public boolean hasReadPermissions(File file){
		return hasPermissions(file,0,"r");
	}
	public boolean hasWritePermissions(File file){
		return hasPermissions(file,1,"w");
	}
	public boolean hasExecutePermissions(File file){
		return hasPermissions(file,2,"x");
	}
	public boolean hasDeletePermissions(File file){
		return hasPermissions(file,3,"d");
	}

	protected long minutesSessionExpires(){
		return 120;
	}
	
	public boolean validateAccessTime(DateTime lastUsed){
		
		DateTime currentTime = new DateTime();
	
		//represents the amount of time between currentTime and limitTime
		Duration interval = new Duration(lastUsed, currentTime );

		long minutesGoneBy = interval.getStandardMinutes();

		boolean valid = (minutesGoneBy < this.minutesSessionExpires());
		return valid;
	}
}
