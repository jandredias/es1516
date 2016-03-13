package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class FileContentsVisitor implements Visitor {

	private String fileContents;
	
	@Override
	public void visitFile(File f) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitDirectory(Directory d) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitPlainFile(PlainFile p) throws UnsupportedOperationException {
		fileContents = p.getContent();
	}

	@Override
	public void visitLink(Link l) throws UnsupportedOperationException {
		fileContents = l.getContent();
	}

	@Override
	public void visitApplication(Application a) throws UnsupportedOperationException {
		fileContents = a.getContent();
	}

	@Override
	public void visitUser(User u) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitRoot(Root r) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitMyDrive(MyDrive mD) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	public String getFileContents() {
		return fileContents;
	}

}
