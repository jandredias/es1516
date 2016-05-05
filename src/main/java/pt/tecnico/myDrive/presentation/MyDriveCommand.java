package pt.tecnico.myDrive.presentation;

public abstract class MyDriveCommand extends Command {
	public MyDriveCommand(MyDriveShell sh, String n) { super(sh, n); }
	public MyDriveCommand(MyDriveShell sh, String n, String h) { super(sh, n, h); }
}