package pt.tecnico.myDrive.presentation;

import java.util.ArrayList;

import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.ExecuteFileService;

public class Execute extends MyDriveCommand {

	public Execute(Shell sh){ super(sh, "do", "execute file on path received as argument 1, possible arguments come after it"); }
	public void execute(String[] args){
		
		if (args.length < 1)
			throw new RuntimeException("USAGE: "+name()+ " <path> [<args>]");
		else{
			long current_token = 0;
			ArrayList<String> functionArgs = new ArrayList<String>();
			
			int i = 1;
			while(i < args.length){
				functionArgs.add(args[i]);
				i++;
			}
			
			ExecuteFileService service = new ExecuteFileService(current_token, args[0], functionArgs);
			try{
				service.execute();
			//FIXME: miguel-amaral: catch the others exceptions after pBucho finish everything 
			}catch (InvalidTokenException e){
				System.out.println("Session Expired");
			}catch (FileNotFoundException e){
				System.out.println("Error: File"+ args[0] + " Does Not Exists..");
			}catch (MyDriveException e) {
				System.out.println("Something critical went Wrong: " + e.getClass() + " : " + e.getMessage());
			}
		}
		
	}
}