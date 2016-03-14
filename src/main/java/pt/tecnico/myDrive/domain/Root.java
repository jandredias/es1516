package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

public class Root extends Root_Base {
    
    public Root() {
        init("root", "***", "Super User", 11111010, null);
    }
    
  	public Root(Element xml) {
		this.xmlImport(xml);
	}
	
	protected void xmlImport(Element xml) {
		super.xmlImport(xml);
	}	
	
    public Element xmlExport() {
     	Element element = super.xmlExport();
     	
     	element.setName("root");

     	return element;
    }
}
