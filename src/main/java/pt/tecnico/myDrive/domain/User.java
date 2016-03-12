package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

public class User extends User_Base {
    
    protected User() {/*for subclasses to use*/}

    public User(String username, String pwd, String name, Integer permissions, pt.tecnico.myDrive.domain.Directory home) {
	init(username, pwd, name, permissions, home);
    }
    public User(String username, pt.tecnico.myDrive.domain.Directory home){
    	init(username, username, username, 11111010, home);
    }

    protected void init(String username, String pwd, String name, Integer permissions, pt.tecnico.myDrive.domain.Directory home) {
    	setUsername(username);
        setPassword(pwd);
        setName(name);
        setPermissions(permissions);
        setUsersHome(home);
    }
    
    public Element xmlExport() {
     	Element element = new Element("user"); 
     	
     	element.setAttribute("username", getUsername());
     	element.setAttribute("password", getPassword());
     	element.setAttribute("name", getName());
     	element.setAttribute("permissions", Integer.toString(getPermissions()));
     	element.setAttribute("home",getUsersHome().getPath());
     	
     	return element;
    }
}
