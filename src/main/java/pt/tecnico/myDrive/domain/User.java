package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

public class User extends User_Base {

    protected User() {/*for subclasses to use*/}

    public User(String username, String pwd, String name, Integer permissions, pt.tecnico.myDrive.domain.Directory home) {
    	init(username, pwd, name, permissions, home);
    }
    public User(String username, Directory home){
    	init(username, username, username, 11110000, home);
    }

    protected void init(String username, String pwd, String name, Integer permissions, pt.tecnico.myDrive.domain.Directory home) {
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
		//TODO Auto generated stub
	}

    public Element xmlExport() {
     	Element element = new Element("user");
     	element.setAttribute("username", getUsername());

     	Element passwordElement = new Element("password");
     	passwordElement.addContent(getPassword());

     	Element nameElement = new Element("name");
     	nameElement.addContent(getName());

     	Element permissionsElement = new Element("mask");
     	permissionsElement.addContent(Integer.toString(getPermissions()));

     	Element homeElement = new Element("home");
     	homeElement.addContent(getUsersHome().getPath());

     	element.addContent(passwordElement);
     	element.addContent(nameElement);
     	element.addContent(permissionsElement);
     	element.addContent(homeElement);


     	return element;
    }

	public void remove() {
    //TODO
	}
}
