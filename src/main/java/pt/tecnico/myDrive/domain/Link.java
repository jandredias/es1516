package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.PrivateResourceException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import java.util.ArrayList;

public class Link extends Link_Base {

	public Link(){ super(); }

	public Link(String name, User owner, String content) 
			throws FileExistsException, InvalidFileNameException{

		init(name, owner, content);
	}

	public Link(Element xml) throws FileExistsException, InvalidFileNameException {
		this.xmlImport(xml);
	}

	protected void xmlImport(Element xml){
		super.xmlImport(xml);
	}

	protected void importContent(Element xml) {
		setContent(xml.getChild("value").getValue());
	}

	public ArrayList<Element> xmlExport() {
		ArrayList<Element> array = super.xmlExport();
		array.get(0).setName("link");

		Element methotdElement = array.get(0).getChild("contents");
		methotdElement.setName("value");

		return array;
	}
	public void accept(Visitor visitor) throws UnsupportedOperationException {
		visitor.visitLink(this);
	}
	
	@Override
	public void setContent(String newContent) throws PrivateResourceException{
		throw new PrivateResourceException("Link content cannot be changed");
	}
}
