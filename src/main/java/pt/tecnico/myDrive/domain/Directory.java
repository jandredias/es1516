package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.FileNotFoundException;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;

public class Directory extends Directory_Base {
    
    public Directory(String name, Integer id, DateTime modification, Integer permissions, User owner, Directory father) {
        init(name, id, modification, permissions, owner);
        this.setDir(father);
    }


    public void accept(Visitor visitor) throws UnsupportedOperationException {
    	visitor.visitDirectory(this);
    }

    public File getFile(String fileName) throws FileNotFoundException {
    	for(File file: getFilesSet())
    		if(file.getName().equals(fileName))
    			return file;
    	throw new FileNotFoundException();
    }
    
   /* FIXME */ 
   
    public Directory getDirectory(String fileName) {
    	for(File file: getFilesSet())
    		if(file.getName().equals(fileName) &&  file.getClass() == Directory.class)
    			return (Directory) file;
    	return null; //FIXME
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
    
    @Override
    public boolean hasFile(String fileName) throws UnsupportedOperationException {
    	try {
    		getFile(fileName);
    	} catch (FileNotFoundException e) {
    		return false;
    	}
    	return true;
    }
    
    public void addFile(File fileToBeAdded) throws FileAlreadyExistsException {
        if (hasContact(fileToBeAdded.getName()))
            throw new NameAlreadyExistsException(contactToBeAdded.getName());

        super.addFile(fileToBeAdded);
    }
}

