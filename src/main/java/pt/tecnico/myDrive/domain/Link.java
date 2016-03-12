package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class Link extends Link_Base {

    public Link(){super();}
    
    public void accept(Visitor visitor) throws UnsupportedOperationException {
    	visitor.visitLink(this);
    }
}
