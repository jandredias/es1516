package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

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
}
