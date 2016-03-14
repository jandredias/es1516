package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import java.util.ArrayList;

public class Link extends Link_Base {

    public Link(){ super(); }

    public Link(String name, Integer id, DateTime modification,
    Integer permissions, User owner, String content, Directory father) throws FileExistsException{
   	  init(name, id, modification, permissions, owner, content, father);
    }

    public Link(Element xml, User owner, Directory parent)throws FileExistsException {
		    this.xmlImport(xml, owner, parent);
    }

	protected void xmlImport(Element xml, User owner, Directory parent) throws FileExistsException{
		super.xmlImport(xml, owner, parent);
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
}
