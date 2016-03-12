package pt.tecnico.myDrive.domain;

import java.util.Arrays;
import java.util.ArrayList;

public class MyDrive extends MyDrive_Base {
    
    public MyDrive() {
        super();
    }
    
    public File getFileFromPath(String path) { /*recebe um path (string) e devolve o ficheiro correspondente caso exista*/
        ArrayList<String> pieces = splitString(path);
        File currentFile = getRootDirectory();
        for(String currentPiece: pieces)
		//TODO try catch isNotDirectoryException
            currentFile = currentFile.getFile(currentPiece);
        return currentFile;
    }

    private ArrayList<String> splitString(String s){
	ArrayList<String> pieces = new ArrayList<String>(Arrays.asList(s.split("/")));
	return pieces;
    }
}
