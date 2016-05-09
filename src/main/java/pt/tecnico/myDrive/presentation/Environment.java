package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.AddVariableService;

public class Environment extends MyDriveCommand {

	/**
	 * @param sh MyDriveShell
	 */
	public Environment(MyDriveShell sh){
		super(sh, "new", "login a user");
	}
	
	/**
	 * @param String[] args
	 */
	public void execute(String[] args){
		long currentToken = ((MyDriveShell) shell()).getCurrentToken();
		
		switch(args.length){
		case 0:
			//TODO
			break;
		case 1:
			//TODO
			//MyDrive.getInstance()
			break;
		case 2:
			AddVariableService addVariableService = new AddVariableService(currentToken, args[0], args[1]);
			try{
				addVariableService.execute();
			}catch(MyDriveException e){
				System.out.println("Could not create variable: " + e.getMessage());
				e.printStackTrace();
			}
			break;
		default:
			throw new RuntimeException("Invalid syntax. Usage: env [name [value]]");
		}
	}
}