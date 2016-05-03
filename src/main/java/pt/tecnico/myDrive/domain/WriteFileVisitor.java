package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

public class WriteFileVisitor extends Visitor {
	
	private String content;
	//private User _visiter -> from super class
	public WriteFileVisitor(User u){
		super(u);
	}
	
	public WriteFileVisitor(String c, User u){
		this(u);
		this.content = c;
	}

	@Override
	public void visitFile(File f) throws UnsupportedOperationException, PermissionDeniedException {
		throw new UnsupportedOperationException("Shouldn't happen.");
	}

	@Override
	public void visitDirectory(Directory d) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't write to directories.");
	}

	@Override
	public void visitPlainFile(PlainFile p) throws UnsupportedOperationException, PermissionDeniedException {
		if(_visiter.hasWritePermissions(p))
			p.setContent(content);
		else
			throw new PermissionDeniedException("O utilizador " + _visiter + "nao tem permissoes de escrita para o ficheiro");
	}

	@Override
	public void visitLink(Link l) throws UnsupportedOperationException, PermissionDeniedException {
		throw new UnsupportedOperationException("\u001B[31;1mCan't execute Link: Probably domain is wrong!\u001B[0m");
	}

	@Override
	public void visitApplication(Application a) throws UnsupportedOperationException, PermissionDeniedException {
		if(_visiter.hasWritePermissions(a))
			a.setContent(content);
		else
			throw new PermissionDeniedException("O utilizador " + _visiter + "nao tem permissoes de escrita para o ficheiro");
	}

	@Override
	public void visitUser(User u) throws UnsupportedOperationException, PermissionDeniedException {
		throw new UnsupportedOperationException("Can't write users");
	}

	@Override
	public void visitRoot(Root r) throws UnsupportedOperationException, PermissionDeniedException {
		throw new UnsupportedOperationException("Can't write root");
	}

	@Override
	public void visitMyDrive(MyDrive mD) throws UnsupportedOperationException, PermissionDeniedException {
		throw new UnsupportedOperationException("Can't write MyDrive");
	}

}
