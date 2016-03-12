package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;

public class Directory extends Directory_Base {
    
    public Directory(String name, Integer id, DateTime modification, Integer permissions, pt.tecnico.myDrive.domain.User owner, pt.tecnico.myDrive.domain.Directory father) {
        init(name, id, modification, permissions, owner);
        this.setDir(father);
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
    
   /* FIX ME */ 
   
    public Directory getDirectory(String fileName) {
    	for(File file: getFilesSet())
    		if(file.getName().equals(fileName) &&  file.getClass() == Directory.class)
    			return (Directory) file;
    	return null;
    }
    
    @Override
    public void deleteFile() throws NotDirectoryException, DirectoryIsNotEmptyException {
    	if(getFilesSet().isEmpty())
    		super.deleteFile();
    	throw new DirectoryIsNotEmptyException();
    		
    	
    }
}
