package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.NotPlainFileException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.service.WriteFileService;
import pt.tecnico.myDrive.service.ChangeDirectoryService;
public class Write extends MyDriveCommand {

	public Write(Shell sh){ super(sh, "update", "changes a file's content"); }
	public void execute(String[] args){
		if (args.length < 1)
			throw new RuntimeException("USAGE: "+name()+ " <path> <text>");
		else{
			long token = shell().getCurrentToken();
			try {
				WriteFileService wf = new WriteFileService(token, args[0], args[1]);
				wf.execute();
			}catch (InvalidTokenException e){
				System.out.println("Session Expired");
			}catch (FileNotFoundException e){
				System.out.println("Error: File " + args[0] + " not found");
			}catch (PermissionDeniedException e){
				System.out.print("Error: Permission Denied");
			}catch (UnsupportedOperationException e){
				System.out.print("Error: Permission Denied");
			}catch (NotDirectoryException e){
				System.out.print("Invalid Path: not a directory");
			}catch (MyDriveException e){
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

			}
			
		}
	}
