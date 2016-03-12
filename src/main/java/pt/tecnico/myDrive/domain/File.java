package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.NotDirectoryException;

public class File extends File_Base {
    
    protected File() { /* for deriver classes */}
    
    public File(String name, Integer id, DateTime modification, Integer permissions, pt.tecnico.myDrive.domain.User owner){
    	init(name, id, modification, permissions, owner);
    }

    protected void init(String name, Integer id, DateTime modification, Integer permissions, pt.tecnico.myDrive.domain.User owner){
    	setName(name);
    	setId(id);
    	setModification(modification);
    	setPermissions(permissions);
    	setOwner(owner);
    }

    public File getFile(String fileName) throws NotDirectoryException { throw new NotDirectoryException(); }

    public void deleteFile() throws NotDirectoryException, DirectoryIsNotEmptyException {
    	Directory parentDirectory = getDir();
    	parentDirectory.removeFiles(this);
    	
    }
}