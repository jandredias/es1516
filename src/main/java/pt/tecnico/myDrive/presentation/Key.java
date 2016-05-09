package pt.tecnico.myDrive.presentation;

public class Key extends MyDriveCommand {

	public Key(MyDriveShell sh) {
		super(sh, "token", "changes between users");
	}

	public void execute(String[] args) {
		// TODO
		MyDriveShell sh = (MyDriveShell) shell();
		if (args.length < 0)
			throw new RuntimeException("USAGE: " + name() + " [username]");
		else {
			if (args.length == 0) {
				long token = sh.getCurrentToken();
				System.out.println("Username: " + sh.getUsernameByToken(token));
				System.out.println("Token: " + token);
			} else {
				long newToken = sh.getTokenByUsername(args[0]);
				sh.setCurrentToken(newToken);
				// FIXME update token?
				System.out.print("Token: " + newToken);

			}
		}
	}
}