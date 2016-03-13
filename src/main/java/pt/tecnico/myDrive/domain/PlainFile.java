package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.FileAlreadyExistsException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import java.util.ArrayList;

public class PlainFile extends PlainFile_Base {

    protected PlainFile() { /*for derived classes*/}

	public PlainFile(String name, Integer id, DateTime modification, Integer permissions, User owner, String content, Directory father) throws FileAlreadyExistsException{
		init(name, id, modification, permissions, owner,content, father);
    }
	
	public void init(String name, Integer id, DateTime modification, Integer permissions, User owner, String content, Directory father) throws FileAlreadyExistsException {
		super.init(name, id, modification, permissions, owner, father); //File Init
		this.setContent(content);
	}

     public ArrayList<Element> xmlExport() {
       ArrayList<Element> array = super.xmlExport();
        array.get(0).setName("plain-file");
        array.get(0).setAttribute("content",getContent());
        return array;
     }
     public void accept(Visitor visitor) throws UnsupportedOperationException {
     	visitor.visitPlainFile(this);
     }
}
