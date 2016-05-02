package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.service.ListDirectoryService;

public class List extends MyDriveCommand {

	public List(Shell sh) {
		super(sh, "ls", "prints the content of the path directory or the current directory if no path is provided");
	}

	public void execute(String[] args) {
		if (args.length > 1)
			throw new RuntimeException("USAGE: " + name() + " <path>");
		else {
			long token = shell().getCurrentToken();
			
			ListDirectoryService service;
			
			if (args.length == 0) {
				// print current dir
				service = new ListDirectoryService(token);
				doAndPrintService(service);
			}
			else if (args.length == 1) {
				// print path dir
				try {
					service = new ListDirectoryService(token, args[0]);
					doAndPrintService(service);
				} catch (FileNotFoundException e) {
					System.out.println("Path: file not found");
				} catch (NotDirectoryException e) {
					System.out.println("Path: file not directory");
				}
			}
		}
	}
	
	private void doAndPrintService(ListDirectoryService service) {
		try {
			service.execute();
		} catch (InvalidTokenException e) {
			System.out.println("Session Expired");
		} catch (PermissionDeniedException e) {
			System.out.println("User does not have permission to list target directory");
		} catch (MyDriveException e) {
			System.out.println("Something critical went Wrong: " + e.getClass() + " : " + e.getMessage());
		}
		
		java.util.List<java.util.List<String>> result = service.result();
		
		for(java.util.List<String> list : result){
			for(String s : list){
				System.out.println(s + " ");
			}
			System.out.println("\n");
		}
	}
}