package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.MyDriveException;

import org.joda.time.DateTime;

public class Directory extends Directory_Base {
    
    public Directory(String name, Integer id, DateTime modification, Integer permissions, pt.tecnico.myDrive.domain.User owner) {
        init(name, id, modification, permissions, owner);
    }

    public void accept(Visitor visitor) throws MyDriveException /*TODO*/{
    	visitor.visitDirectory(this);
    }

    public File getFile(String fileName){/*TODO*/ return null;}
}
