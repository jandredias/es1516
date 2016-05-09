package pt.tecnico.myDrive.presentation;

import java.io.File;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import pt.tecnico.myDrive.service.ImportXMLService;

public class Import extends MyDriveCommand{

    public Import(MyDriveShell sh) { super(sh, "import", "import my drive xml. (use ./locaFile or resourceFile)"); }
    public void execute(String[] args) {
	if (args.length < 1)
	    throw new RuntimeException("USAGE: "+name()+" filename");
	try {
	    SAXBuilder builder = new SAXBuilder();
	    File file;
	    if (args[0].startsWith(".")) file = new File(args[0]);
	    else file = resourceFile(args[0]);
            Document doc = (Document)builder.build(file);
	    new ImportXMLService(doc).execute();
	} catch (Exception e) { throw new RuntimeException(e); }
    }

    public File resourceFile(String filename) {
	 log.trace("Resource: "+filename);
         ClassLoader classLoader = getClass().getClassLoader();
         if (classLoader.getResource(filename) == null) return null;
         return new java.io.File(classLoader.getResource(filename).getFile());
    }
}
