package pt.tecnico.myDrive.presentation;


public class MyDriveShell extends Shell {

	public static void main(String[] args) throws Exception {
		MyDriveShell sh = new MyDriveShell();
		sh.execute();
		
	}

	public MyDriveShell() { // add commands here
		super("MyDrive");
		new LoginUser(this).execute(new String[] {"nobody",""} );
		new ChangeDirectory(this);
		new List(this);
		new Execute(this);
		new Write(this);
		new Environment(this);
		new Key(this);
	}
}
