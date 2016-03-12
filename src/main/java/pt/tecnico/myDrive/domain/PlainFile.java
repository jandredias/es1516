package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.MyDriveException;

public class PlainFile extends PlainFile_Base {
    
     public PlainFile(){super();}
     
     public void accept(Visitor visitor) throws MyDriveException /*TODO*/{
     	visitor.visitPlainFile(this);
     }
}
