package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.InvalidUsernameException;

public class Root extends Root_Base {

	public Root() {
		try {
			init("root", "***", "Super User", "rwxdr-x-");
		} catch (InvalidUsernameException e) {
			//"root" is always a valid username	}
		}
	}

	public Root(Element xml) {
		this.xmlImport(xml);
	}

	protected void xmlImport(Element xml) {
		super.xmlImport(xml);
	}
	
	public Element xmlExport() {
		Element element = super.xmlExport();

		element.setName("user");

		return element;
	}
	
	@Override
	public boolean hasWritePermissions(File file){
		//System.out.println("\u001B[33;1mROOT using its writing privileges\u001B[0m");
		return true;
	}
}
