package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import java.util.ArrayList;

public class PlainFile extends PlainFile_Base {

    protected PlainFile() { /*for derived classes*/ }

	public PlainFile(String name,  DateTime modification, Integer permissions, User owner, String content, Directory father) throws FileExistsException, InvalidFileNameException{
		init(name,  modification, permissions, owner, content, father);
    }

	public PlainFile(Element xml, User owner, Directory parent) throws FileExistsException, InvalidFileNameException {
		this.xmlImport(xml, owner, parent);
	}

	protected void xmlImport(Element xml, User owner, Directory parent) throws FileExistsException, InvalidFileNameException {
		super.xmlImport(xml, owner, parent);
		this.importContent(xml);
	}

	protected void importContent(Element xml) {
    setContent(xml.getChild("contents").getValue());
  }

	public void init(String name, DateTime modification, Integer permissions, User owner, String content, Directory father) throws FileExistsException, InvalidFileNameException {
		super.init(name, modification, permissions, owner, father); //File Init
		this.setContent(content);
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
