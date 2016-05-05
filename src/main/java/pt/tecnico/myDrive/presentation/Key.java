package pt.tecnico.myDrive.presentation;
public class Key extends MyDriveCommand {

	public Key(MyDriveShell sh){ super(sh, "token", "login a user"); }
	public void execute(String[] args){
		//TODO
		if (args.length < 0)
			throw new RuntimeException("USAGE: "+name()+ " [username]");
		else{
			if(args.length == 0){
				//FIXME how to print current username without imports
			}
			else{
				
				long newToken = ((MyDriveShell) shell()).getCurrentToken();
				((MyDriveShell) shell()).setCurrentToken(newToken);
				System.out.print(newToken);
			}
		}
	}
}