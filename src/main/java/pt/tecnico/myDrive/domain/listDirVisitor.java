package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.MyDriveException;
import java.util.ArrayList;
import java.util.Set;

public class listDirVisitor implements Visitor{

    private ArrayList<String> fileNames;
    
    public listDirVisitor() {
        fileNames = new ArrayList();
    }

    public ArrayList<String> getFileNames(){
        return fileNames;
    }

    public void visitFile(File f) throws MyDriveException{ //TODO
        throw new MyDriveException(); //TODO
    }

    public void visitDirectory(Directory d) throws MyDriveException{//TODO
        Set<File> fileList = d.getFiles();

        for(File file : fileList){
            fileNames.add(file.getName());
        }
    }

    public void visitPlainFile(PlainFile p) throws MyDriveException{//TODO
        throw new MyDriveException(); //TODO
    }

    public void visitLink(Link l) throws MyDriveException{//TODO
        throw new MyDriveException(); //TODO
    }

    public void visitApplication(Application a) throws MyDriveException{//TODO
        throw new MyDriveException(); //TODO
    }

    public void visitUser(User u) throws MyDriveException{
	throw new MyDriveException(); //TODO
    }//TODO

    public void visitRoot(Root r) throws MyDriveException{
	throw new MyDriveException(); //TODO
    }//TODO

    public void visitMyDrive(MyDrive mD) throws MyDriveException{
        throw new MyDriveException(); //TODO
    }//TODO
}
