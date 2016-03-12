package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

public class Root extends Root_Base {
    
    public Root() {
        init("root", "***", "Super User", 77, null);
    }
    
    public Element xmlExport() {
     	Element element = super.xmlExport();
     	
     	element.setName("root");

     	return element;
    }
}
