package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

import java.util.ArrayList;
/* HERDA DE PLAINFILE*/

public class Application extends Application_Base {

    public Application() {
        super();
    }

    public ArrayList<Element> xmlExport() {
      ArrayList<Element> array = super.xmlExport();
     	array.get(0).setName("application");
     	return array;
    }
    public void accept(Visitor visitor) throws UnsupportedOperationException {
    	visitor.visitApplication(this);
    }

}
