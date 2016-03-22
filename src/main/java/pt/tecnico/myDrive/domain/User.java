package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.InvalidUsernameException;

import org.apache.commons.lang.StringUtils;
import org.jdom2.Element;

public class User extends User_Base {

	protected User() {/*for subclasses to use*/}

	protected User(String username, String pwd, String name, Integer permissions) throws InvalidUsernameException {
		init(username, pwd, name, permissions);
	}

	protected User(String username) throws InvalidUsernameException{
		init(username, username, username, 11110000);
	}
	
	protected void init(String username, String pwd, String name, 
			Integer permissions) throws InvalidUsernameException{

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
			permissions = 11110000;
		
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

		// FIXME check enunciado default mask
		// FIXME mydrive permissions method probably not needed
		setPermissions((xml.getChild("mask") == null) ?
				11111010 :
					MyDrive.permissions(xml.getChild("mask").getValue()));	
	}

	public Element xmlExport() {
		Element element = new Element("user");
		element.setAttribute("username", getUsername());

		Element passwordElement = new Element("password");
		passwordElement.addContent(getPassword());

		Element nameElement = new Element("name");
		nameElement.addContent(getName());

		Element permissionsElement = new Element("mask");
		permissionsElement.addContent(MyDrive.permissions(getPermissions()));

		Element homeElement = new Element("home");
		homeElement.addContent(getUsersHome().getPath());

		element.addContent(passwordElement);
		element.addContent(nameElement);
		element.addContent(permissionsElement);
		element.addContent(homeElement);


		return element;
	}
	
	public void delete(){
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
}
