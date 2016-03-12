package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

public class Link extends Link_Base {

    public Link(){ super(); }
    
    public Element xmlExport() {
     	Element element = super.xmlExport();
     	
     	element.setName("link");

     	return element;
    }
}
