package pt.tecnico.myDrive.presentation;

import java.util.Arrays;

import pt.tecnico.myDrive.exception.AppExecutionException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.ExecuteFileService;

public class Execute extends MyDriveCommand {

	public Execute(MyDriveShell sh) {
		super(sh, "do", "execute file on path received as argument 1, possible arguments come after it");
	}

	public void execute(String[] args) {

		if (args.length < 1)
			throw new RuntimeException("USAGE: " + name() + " <path> [<args>]");
		else {

			long currentToken = ((MyDriveShell) shell()).getCurrentToken();
			ExecuteFileService service = new ExecuteFileService(currentToken, args[0],
					Arrays.copyOfRange(args, 1, args.length));
			try {
				service.execute();
			} catch (InvalidTokenException e) {
				System.out.println("Session Expired");
			} catch (FileNotFoundException e) {
				System.out.println("Error: File" + args[0] + " Does Not Exists..");
			} catch (AppExecutionException e) {
				System.out.println(e.getMessage());
			} catch (MyDriveException e) {
				System.out.println("Something critical went Wrong: " + e.getClass() + " : " + e.getMessage());
			}
		}

	}
}