package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public interface Visitor{
    
    public void visitFile(File f) throws UnsupportedOperationException;

    public void visitDirectory(Directory d) throws UnsupportedOperationException;

    public void visitPlainFile(PlainFile p) throws UnsupportedOperationException;

    public void visitLink(Link l) throws UnsupportedOperationException;

    public void visitApplication(Application a) throws UnsupportedOperationException;

    public void visitUser(User u) throws UnsupportedOperationException;

    public void visitRoot(Root r) throws UnsupportedOperationException;

    public void visitMyDrive(MyDrive mD) throws UnsupportedOperationException;
}
