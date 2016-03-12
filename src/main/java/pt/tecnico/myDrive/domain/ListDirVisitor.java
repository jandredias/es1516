package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import java.util.ArrayList;
import java.util.Set;

public class ListDirVisitor implements Visitor{

    private ArrayList<String> fileNames;
    
    public ListDirVisitor() {
        fileNames = new ArrayList<String>();
    }

    public ArrayList<String> getFileNames(){
        return fileNames;
    }

    public void visitFile(File f) throws UnsupportedOperationException{ 
        throw new UnsupportedOperationException(); 
    }

    public void visitDirectory(Directory d){

        for(File file : d.getFilesSet()){
            fileNames.add(file.getName());
        }
    }

    public void visitPlainFile(PlainFile p) throws UnsupportedOperationException{
        throw new UnsupportedOperationException(); 
    }

    public void visitLink(Link l) throws UnsupportedOperationException{
        throw new UnsupportedOperationException(); 
    }

    public void visitApplication(Application a) throws UnsupportedOperationException{
        throw new UnsupportedOperationException(); 
    }

    public void visitUser(User u) throws UnsupportedOperationException{
	throw new UnsupportedOperationException(); 
    }

    public void visitRoot(Root r) throws UnsupportedOperationException{
	throw new UnsupportedOperationException(); 
    }

    public void visitMyDrive(MyDrive mD) throws UnsupportedOperationException{
        throw new UnsupportedOperationException(); 
    }
}
