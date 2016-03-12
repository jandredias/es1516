package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class PlainFile extends PlainFile_Base {
    
     public PlainFile(){super();}
     
     public void accept(Visitor visitor) throws UnsupportedOperationException{
     	visitor.visitPlainFile(this);
     }
}
