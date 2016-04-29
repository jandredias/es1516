package pt.tecnico.myDrive.presentation;


import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.UserDoesNotExistsException;
import pt.tecnico.myDrive.exception.WrongPasswordException;
import pt.tecnico.myDrive.service.LoginUserService;

public class LoginUser extends MyDriveCommand {

	public LoginUser(Shell sh){ super(sh, "login", "login a user"); }
	public void execute(String[] args){
		//TODO
		if (!(args.length == 3))
			throw new RuntimeException("USAGE: "+name()+ " <username> [<password>]");
		else{
			LoginUserService lus = new LoginUserService(args[0], args[1]);
			try {
				lus.execute();
			} catch (UserDoesNotExistsException e) {
				// TODO Auto-generated catch block
				System.out.println("Utilizador não existente");
			} catch (WrongPasswordException e) {
				// TODO Auto-generated catch block
				System.out.println("Wrong Password");
			}
			catch (MyDriveException e) {
				// TODO Auto-generated catch block
				System.out.println("Should not happen");
			}
			System.out.println("Your token is: " + lus.result());
		}
	}
}