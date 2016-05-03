package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public abstract class Visitor{
	
    protected User _visiter;
	
	public Visitor(User visiter){
		this._visiter = visiter;
	}
	
    public abstract void visitFile(File f) throws PermissionDeniedException, UnsupportedOperationException;

    public abstract void visitDirectory(Directory d) throws PermissionDeniedException, UnsupportedOperationException;

    public abstract void visitPlainFile(PlainFile p) throws PermissionDeniedException, UnsupportedOperationException;

    public abstract void visitLink(Link l) throws PermissionDeniedException, UnsupportedOperationException;

    public abstract void visitApplication(Application a) throws PermissionDeniedException, UnsupportedOperationException;

    public abstract void visitUser(User u) throws PermissionDeniedException, UnsupportedOperationException;

    public abstract void visitRoot(Root r) throws PermissionDeniedException, UnsupportedOperationException;

    public abstract void visitMyDrive(MyDrive mD) throws PermissionDeniedException, UnsupportedOperationException;
}
