package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.ListDirectoryService;

public class List extends MyDriveCommand {

	public List(Shell sh){ super(sh, "ls", "prints the content of the path directory or the current directory if no path is provided"); }
	public void execute(String[] args){
		//FIXME
		if (args.length > 1)
		    throw new RuntimeException("USAGE: " + name() + " <path>");
		else if(args.length == 0){
			//print current dir
		}
		else if(args.length == 1){
			//print path dir
		}
			
		//long token=0;
		/*Como se vai buscar o token? TODO*/
		//new ListDirectoryService(token).execute();
	}
}