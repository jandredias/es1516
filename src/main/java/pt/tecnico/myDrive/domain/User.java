package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.MyDrive;

import org.jdom2.Element;

public class User extends User_Base {

	protected User() {/*for subclasses to use*/}

	public User(String username, String pwd, String name, Integer permissions, Directory home) {
		init(username, pwd, name, permissions, home);
	}
	public User(String username, Directory home){
		init(username, username, username, 11110000, home);
	}

	protected void init(String username, String pwd, String name, Integer permissions, Directory home) {
		setUsername(username);
		setPassword(pwd);
		setName(name);
		setPermissions(permissions);
		setUsersHome(home);
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
}
