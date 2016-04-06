package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.InvalidUsernameException;

import org.apache.commons.lang.StringUtils;
import org.jdom2.Element;

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
		setPassword(pwd);
		setName(name);
		setPermissions(permissions);
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

	protected void delete(){
		setUsersHome(null);
		//*********************************
		//*********************************
		//FIXME inner files
		this.set$ownedFiles(null);
		//FIXME inner files
		//*********************************
		//*********************************
		this.deleteDomainObject();
	}

	public int compareTo(User u){
		return this.getUsername().compareTo(u.getUsername());
	}

	/**
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
}
