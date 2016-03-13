package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.FileNotFoundException;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.FileAlreadyExistsException;

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
    	throw new FileNotFoundException("File: " + fileName + " not Found");
    }

    public Directory getDirectory(String fileName) throws FileNotFoundException {
    	File directory = getFile(fileName);
    	if (directory.getClass() == Directory.class)
    		return (Directory) directory;
    	else
    		throw new FileNotFoundException("Directory: " + fileName + " not Found");
    }
    
    @Override
    public void deleteFile() throws NotDirectoryException, DirectoryIsNotEmptyException {
    	if(getFilesSet().isEmpty()){
    		super.deleteFile();
    	}
    	else{
        	throw new DirectoryIsNotEmptyException();
    	}

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
    
    public boolean hasFile(String fileName)  {
    	try {
    		getFile(fileName);
    	} catch (FileNotFoundException e) {
    		return false;
    	}
    	return true;
    }
    
    public void addFile(File fileToBeAdded) throws FileAlreadyExistsException {
        if (hasFile(fileToBeAdded.getName()))
            throw new FileAlreadyExistsException(fileToBeAdded.getName());

        super.addFiles(fileToBeAdded);
    }
}

