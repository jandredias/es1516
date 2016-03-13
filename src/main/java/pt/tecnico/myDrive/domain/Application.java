package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.FileAlreadyExistsException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

import java.util.ArrayList;
/* HERDA DE PLAINFILE*/

public class Application extends Application_Base {

    public Application() {
        super();
    }
    
    public Application(String name, Integer id, DateTime modification, Integer permissions, User owner, String content, Directory father) throws FileAlreadyExistsException{
      	 init(name, id, modification, permissions, owner,content, father);
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
