package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public interface Visitor{
    
    public void visitFile(File f) throws MyDriveException;

    public void visitDirectory(Directory d) throws MyDriveException;

    public void visitPlainFile(PlainFile p) throws MyDriveException;

    public void visitLink(Link l) throws MyDriveException;

    public void visitApplication(Application a) throws MyDriveException;

    public void visitUser(User u) throws MyDriveException;

    public void visitRoot(Root r) throws MyDriveException;

    public void visitMyDrive(MyDrive mD) throws MyDriveException;
}
