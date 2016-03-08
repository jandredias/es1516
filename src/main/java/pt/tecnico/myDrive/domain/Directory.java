package pt.tecnico.myDrive.domain;

#import "Visitor.java"

public class Directory extends Directory_Base {
    
    public Directory() {
        super();
    }

    public accept(Visitor v){
        v.visitDirectory(this);
    }    
}
