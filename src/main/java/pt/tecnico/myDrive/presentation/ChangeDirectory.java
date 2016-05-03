package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.ChangeDirectoryService;

public class ChangeDirectory extends MyDriveCommand {

	public ChangeDirectory(Shell sh) {
		super(sh, "cwd", "changes the current working directory");
	}

	public void execute(String[] args) {

		if (args.length < 1) {
			throw new RuntimeException("Invalid syntax. Usage: cwd [path]");
		}

		long currentToken = shell().getCurrentToken();
		ChangeDirectoryService cdService = new ChangeDirectoryService(currentToken, args[0]);

		try {
			cdService.execute();
			System.out.println("Current directory: " + cdService.result());
		} catch (MyDriveException e) {
			System.out.println("Could not change directory: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
}
