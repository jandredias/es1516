package pt.tecnico.myDrive.domain;

import java.util.ArrayList;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class PlainFile extends PlainFile_Base {

	protected PlainFile() { /*for derived classes*/ }

	public PlainFile(String name, User owner, String content) 
			throws FileExistsException, InvalidFileNameException {

		init(name, owner, content);
	}

	public void init(String name, User owner, String content) 
			throws FileExistsException, InvalidFileNameException {

		super.init(name, owner); //File Init
		this.setContent(content);
	}
	public PlainFile(Element xml) {
		this.xmlImport(xml);
	}

	protected void xmlImport(Element xml) {
		super.xmlImport(xml);
		this.importContent(xml);
	}

	/**
	 * Method that every subclass will override
	 * @param xml
	 */
	protected void importContent(Element xml) {
		setContent(xml.getChild("contents").getValue());
	}

	public ArrayList<Element> xmlExport() {
		
		ArrayList<Element> array = super.xmlExport();
		array.get(0).setName("plain");

		Element contentsElement = new Element("contents");
		contentsElement.addContent(getContent());
		array.get(0).addContent(contentsElement);

		return array;
	}
	
	public void accept(Visitor visitor) throws UnsupportedOperationException {
		visitor.visitPlainFile(this);
	}
}
