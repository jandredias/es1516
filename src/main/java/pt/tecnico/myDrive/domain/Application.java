package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

import java.util.ArrayList;

/* HERDA DE PLAINFILE*/
public class Application extends Application_Base {

	public Application() { super(); }

	public Application(String name, User owner, String content) 
			throws FileExistsException, InvalidFileNameException{
		
		init(name, owner, content);
	}

	public Application(Element xml) 
			throws FileExistsException, InvalidFileNameException{
		
		this.xmlImport(xml);
	}

	protected void xmlImport(Element xml) {
		super.xmlImport(xml);
	}

	protected void importContent(Element xml) {
		setContent(xml.getChild("method").getValue());
	}


	public ArrayList<Element> xmlExport() {
		ArrayList<Element> array = super.xmlExport();
		array.get(0).setName("app");

		Element methotdElement = array.get(0).getChild("contents");
		methotdElement.setName("method");

		return array;
	}
	
	public void accept(Visitor visitor) throws UnsupportedOperationException {
		visitor.visitApplication(this);
	}

}
