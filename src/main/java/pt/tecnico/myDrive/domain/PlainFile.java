package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.FileAlreadyExistsException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import java.util.ArrayList;

public class PlainFile extends PlainFile_Base {

    protected PlainFile() { /*for derived classes*/ }

	public PlainFile(String name, Integer id, DateTime modification, Integer permissions, User owner, String content, Directory father) throws FileAlreadyExistsException{
		init(name, id, modification, permissions, owner, content, father);
    }
	
	public PlainFile(Element xml) {
		this.xmlImport(xml);
	}
	
	protected void xmlImport(Element xml) {
		super.xmlImport(xml);
		this.importContent(xml); //TODO Template Method
	}

	protected void importContent(Element xml) {
		// TODO Auto-generated method stub
		
	}

	public void init(String name, Integer id, DateTime modification, Integer permissions, User owner, String content, Directory father) throws FileAlreadyExistsException {
		super.init(name, id, modification, permissions, owner, father); //File Init
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
