package pt.tecnico.myDrive.system;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.presentation.*;
import pt.tecnico.myDrive.presentation.MyDriveShell;
import pt.tecnico.myDrive.service.AbstractServiceTest;

public class SystemTest extends AbstractServiceTest {

    private MyDriveShell sh;

    protected void populate() {
        sh = new MyDriveShell();
    }

    @Test
    public void success() {
        /*new LoginUser(sh).execute(new String[] { "user pass" } );
        new ChangeDirectory(sh).execute(new String[] { "path" } );
        new List(sh).execute(new String[] { "" } );
        new Execute(sh).execute(new String[] { "path" } );
        new Write(sh).execute(new String[] { "path text" } );
        new Environment(sh).execute(new String[] { "name value" } );
        new Key(sh).execute(new String[] { "token" } );*/
    }
    
    /*TODO After test teardown, clean database*/
    
    public void tearDown(){
    	MyDrive.getInstance().cleanup();
    }
} 
