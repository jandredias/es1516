package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.UserDoesNotExistsException;
import pt.tecnico.myDrive.exception.WrongPasswordException;
import pt.tecnico.myDrive.service.LoginUserService;

public class LoginUser extends MyDriveCommand {

	public LoginUser(Shell sh) {
		super(sh, "login", "login a user");
	}

	public void execute(String[] args){
		//TODO
		if (args.length > 2 || args.length == 0)
			throw new RuntimeException("USAGE: "+name()+ " <username> [<password>]");
		else{
			//TODO args[1] : exceotuion out of bounds thrown 
			LoginUserService lus;
			if(args.length == 2)
				 lus = new LoginUserService(args[0], args[1]);
			else
				 lus = new LoginUserService(args[0], "");
			try {
				lus.execute();
			} catch (UserDoesNotExistsException e) {
				// TODO Auto-generated catch block
				System.out.println("Utilizador não existente");
			} catch (WrongPasswordException e) {	
				// TODO Auto-generated catch block
				System.out.println("Wrong Password");
			}
			//FIXME: Tagarito two more catches
			catch (MyDriveException e) {
				System.out.println("Something critical went Wrong: " + e.getClass() + " : " + e.getMessage());
			}
			shell().addUserToken(args[0], lus.result());
		}
	}
}