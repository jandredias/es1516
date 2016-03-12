package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exception.UnsupportedOperationException;

/* HERDA DE PLAINFILE*/

public class Application extends Application_Base {
    
    public Application() {
        super();
    }
    
    public void accept(Visitor visitor) throws UnsupportedOperationException {
    	visitor.visitApplication(this);
    }
    
}
