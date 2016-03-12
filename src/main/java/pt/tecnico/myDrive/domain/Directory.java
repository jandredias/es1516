package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.MyDriveException;

public class Directory extends Directory_Base {
    
    public Directory() {
        super();
    }

    public void accept(Visitor v) throws MyDriveException{
	v.visitDirectory(this);
    }
}
