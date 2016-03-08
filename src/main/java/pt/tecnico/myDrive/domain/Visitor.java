package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.MyDriveException;

public interface Visitor{
    
    public void visitFile(File f) throws MyDriveException;//TODO

    public void visitDirectory(Directory d) throws MyDriveException;//TODO

    public void visitPlainFile(PlainFile p) throws MyDriveException;//TODO

    public void visitLink(Link l) throws MyDriveException;//TODO

    public void visitApplication(Application a) throws MyDriveException;//TODO

    public void visitUser(User u) throws MyDriveException;//TODO

    public void visitRoot(Root r) throws MyDriveException;//TODO

    public void visitMyDrive(MyDrive mD) throws MyDriveException;//TODO
}
