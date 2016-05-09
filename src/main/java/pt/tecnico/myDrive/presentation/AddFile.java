package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.CreateFileService;

public class AddFile extends MyDriveCommand {

	public AddFile(MyDriveShell sh) {
		super(sh, "touch", "adds file to domain");
	}

	public void execute(String[] args) {

		if (args.length < 2)
			throw new RuntimeException("USAGE: " + name() + " <fileName> <fileType> [<content>]");
		else {

			long currentToken = ((MyDriveShell) shell()).getCurrentToken();
			CreateFileService service = null;
			if (args.length == 3)
				service = new CreateFileService(currentToken, args[0], args[1], args[2]);
			else
				service = new CreateFileService(currentToken, args[0], args[1], "");
			try {
				service.execute();
			} catch (InvalidTokenException e) {
				System.out.println("Session Expired");
			} catch (MyDriveException e) {
				System.out.println("Something critical went Wrong: " + e.getClass() + " : " + e.getMessage());
			}
		}

	}
}