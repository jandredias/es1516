package pt.tecnico.myDrive.domain;

import java.util.ArrayList;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidLinkContentException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.PrivateResourceException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class Link extends Link_Base {

	public Link(){ super(); }

	public Link(String name, User owner, String content) 
			throws FileExistsException, InvalidFileNameException, InvalidLinkContentException{
		if(content.contains("\0"))
			throw new InvalidLinkContentException("Contains \0 char..");
		if(content.equals(""))
			throw new InvalidLinkContentException("empty content in link invalid");
		init(name, owner, content);
	}

	public Link(Element xml) throws FileExistsException, InvalidFileNameException {
		this.xmlImport(xml);
	}

	protected void xmlImport(Element xml){
		super.xmlImport(xml);
	}

	protected void importContent(Element xml) {
		super.setContent(xml.getChild("value").getValue());
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
	
	public File getFile(User user) throws FileNotFoundException, PermissionDeniedException{
		if(!user.hasExecutePermissions(this)) throw new PermissionDeniedException("On link " + this.getName());
		String content = this.getContent();
		if(content.charAt(0) == '/')
			return MyDrive.getInstance().getFile(content,user);
		else
			return this.getDir().getFile(content, user);
	}
}
