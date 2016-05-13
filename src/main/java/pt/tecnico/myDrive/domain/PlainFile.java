package pt.tecnico.myDrive.domain;

import java.util.ArrayList;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
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
		super.setContent(content);
	}
	public PlainFile(Element xml) {
		this.xmlImport(xml);
	}

	protected void xmlImport(Element xml) {
		super.xmlImport(xml);
		this.importContent(xml);
	}
	
	public void setContent(String content, User user){
		user.hasWritePermissions(this);
		this.setContent(content);
	}
	
	
	/**
	 * Method that every subclass will override
	 * @param xml
	 */
	protected void importContent(Element xml) {
		setContent((xml.getChild("contents") == null) ?
				"" :
					xml.getChild("contents").getValue());
	}

	public ArrayList<Element> xmlExport() {
		
		ArrayList<Element> array = super.xmlExport();
		array.get(0).setName("plain");

		Element contentsElement = new Element("contents");
		contentsElement.addContent(getContent());
		array.get(0).addContent(contentsElement);

		return array;
	}
	
	public void accept(Visitor visitor) throws UnsupportedOperationException, PermissionDeniedException {
		visitor.visitPlainFile(this);
	}
}
