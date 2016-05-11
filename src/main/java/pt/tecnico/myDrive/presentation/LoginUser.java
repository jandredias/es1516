package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.UserDoesNotExistsException;
import pt.tecnico.myDrive.exception.WrongPasswordException;
import pt.tecnico.myDrive.service.LoginUserService;

public class LoginUser extends MyDriveCommand {

	public LoginUser(MyDriveShell sh) {
		super(sh, "login", "login a user");
	}

	public void execute(String[] args) {

		if (args.length > 2 || args.length == 0)
			throw new RuntimeException("USAGE: " + name() + " <username> [<password>]");
		else {
			LoginUserService lus;
			if (args.length == 2)
				lus = new LoginUserService(args[0], args[1]);
			else
				lus = new LoginUserService(args[0], "");
			try {
				lus.execute();
			} catch (UserDoesNotExistsException e) {
				System.out.println("non existing user:" + args[0]);
			} catch (WrongPasswordException e) {
				System.out.println("Wrong Password");
			} catch (MyDriveException e) {
				System.out.println("Something critical went Wrong: " + e.getClass() + " : " + e.getMessage());
			}
			MyDriveShell shell = (MyDriveShell) shell();
			shell.addUserToken(args[0], lus.result());
			shell.setCurrentToken(lus.result());
			if (lus.result() != shell.getTokenByUsername("nobody")) {
				shell.removeGuest();
			}
		}
	}
}