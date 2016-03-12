package pt.tecnico.myDrive.domain;

public class User extends User_Base {
    
    protected User() {/*for subclasses to use*/}

    public User(String username, String pwd, String name, Integer permissions, pt.tecnico.myDrive.domain.Directory home) {
	init(username, pwd, name, permissions, home);
    }

    protected void init(String username, String pwd, String name, Integer permissions, pt.tecnico.myDrive.domain.Directory home) {
	setUsername(username);
        setPassword(pwd);
        setName(name);
        setPermissions(permissions);
        setUsersHome(home);
    }
    
}
