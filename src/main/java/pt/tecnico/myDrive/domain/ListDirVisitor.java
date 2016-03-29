package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import java.util.ArrayList;
import java.util.Collections;

public class ListDirVisitor implements Visitor{

    private ArrayList<String> fileNames;
    
    public ListDirVisitor() {
        fileNames = new ArrayList<String>();
    }

    public ArrayList<String> getFileNames(){
        return fileNames;
    }

    public void visitFile(File f) throws UnsupportedOperationException{ 
        throw new UnsupportedOperationException("Can't list File."); 
    }

    public void visitDirectory(Directory d){
    	for(File file : d.getFilesSet()){
    		if(file.getName() != "/")
    			fileNames.add(file.getName());
    	}

		Collections.sort(fileNames);
		
		fileNames.add(0, ".");
		fileNames.add(1, "..");
    }

    public void visitPlainFile(PlainFile p) throws UnsupportedOperationException{
        throw new UnsupportedOperationException("Can't list PlainFile."); 
    }

    public void visitLink(Link l) throws UnsupportedOperationException{
        throw new UnsupportedOperationException("Can't list Link."); 
    }

    public void visitApplication(Application a) throws UnsupportedOperationException{
        throw new UnsupportedOperationException("Can't list Application."); 
    }

    public void visitUser(User u) throws UnsupportedOperationException{
	throw new UnsupportedOperationException("Can't list User."); 
    }

    public void visitRoot(Root r) throws UnsupportedOperationException{
	throw new UnsupportedOperationException("Can't list Root."); 
    }

    public void visitMyDrive(MyDrive mD) throws UnsupportedOperationException{
        throw new UnsupportedOperationException("Can't list MyDrive."); 
    }
}
