package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

/* HERDA DE PLAINFILE*/

public class Application extends Application_Base {
    
    public Application() {
        super();
    }
    
    public Element xmlExport() {
     	Element element = super.xmlExport();
     	
     	element.setName("application");

     	return element;
    }
    public void accept(Visitor visitor) throws UnsupportedOperationException {
    	visitor.visitApplication(this);
    }
    
}
