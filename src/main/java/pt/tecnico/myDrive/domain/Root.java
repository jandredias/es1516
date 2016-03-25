package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.InvalidUsernameException;

public class Root extends Root_Base {

	public Root() {
		try {
			init("root", "***", "Super User", "rwxd----");
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
}
