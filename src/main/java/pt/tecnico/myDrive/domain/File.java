package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

public class File extends File_Base {
    
    protected File() { /* for deriver classes */}
    
    public File(String name, Integer id, DateTime modification, Integer permissions, pt.tecnico.myDrive.domain.User owner) {
    	init(name, id, modification, permissions, owner);
    }

    protected void init(String name, Integer id, DateTime modification, Integer permissions, pt.tecnico.myDrive.domain.User owner) {
    	setName(name);
		setId(id);
		setModification(modification);
		setPermissions(permissions);
		setOwner(owner);
    }

    public File getFile(String fileName){/*FIXME throw new InvalidOperationException();*/ return null;}
    
    public Element xmlExport() {
    	Element element = new Element("file");
    	element.setAttribute("name",getName());
    	element.setAttribute("id",getId().toString());
    	element.setAttribute("modification",getModification().toString());
    	element.setAttribute("permissions",getPermissions().toString());
    	return element;
    }
}
