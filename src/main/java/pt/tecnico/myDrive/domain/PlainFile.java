package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

public class PlainFile extends PlainFile_Base {
    
     public PlainFile(){super();}
     
     
     public Element xmlExport() {
     	Element element = super.xmlExport();
     	
     	element.setName("plain-file");
     	element.setAttribute("content",getContent());
     	return element;
     }
}
