package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;

public class Directory extends Directory_Base {
    
    public Directory(String name, Integer id, DateTime modification, Integer permissions, pt.tecnico.myDrive.domain.User owner) {
        init(name, id, modification, permissions, owner);
    }


    public void accept(Visitor visitor) throws UnsupportedOperationException {
    	visitor.visitDirectory(this);
    }

    public File getFile(String fileName) {
    	for(File file: getFilesSet())
    		if(file.getName().equals(fileName))
    			return file;
    	return null;
    }
    
    @Override
    public void deleteFile() throws NotDirectoryException, DirectoryIsNotEmptyException {
    	if(getFilesSet().isEmpty())
    		super.deleteFile();
    	throw new DirectoryIsNotEmptyException();
    }
    
    public Element xmlExport() {
     	Element element = super.xmlExport();
     	
     	element.setName("directory");
     	
     	Element filesElement = new Element("files");
        element.addContent(filesElement);

        for (File file: getFilesSet())
            filesElement.addContent(file.xmlExport());
        return element;
     }
}

