
package pt.tecnico.myDrive.presentation;
public class Key extends MyDriveCommand {

	public Key(Shell sh){ super(sh, "token", "changes between sessions"); }
	public void execute(String[] args){
		//TODO
		if (args.length < 0)
			throw new RuntimeException("USAGE: "+name()+ " [username]");
		else{
			if(args.length == 0){
				long token = shell().getCurrentToken();
				System.out.println("Username: " + shell().getUsernameByToken(token));
				System.out.println("Token: " + token);
			}
			else{
				long newToken = shell().getTokenByUsername(args[0]);
				shell().setCurrentToken(newToken);
				//FIXME update token?
				System.out.print("Token: " + newToken);
			}
		}
	}
}