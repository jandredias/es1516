package pt.tecnico.myDrive.domain;

import java.util.ArrayList;

class DeleteService {
    Manager _manager;
    String _path;

    public DeleteService(Manager manager, String path){
        _manager = manager;
	_path = path;
    }

    public void execute(){
      //TODO
 	File file = getFileFromPath(getPath());
	file.accept(this);
    }

    @Override
    public void visitDirectory(Directory directory){
	if(directoryIsEmpty)
	   visitFile(directory);
	else
	   //TODO directoryIsNotEmpty
	   throw toDoException;
    }

    public void visitFile(File file){
	Directory father = file.getDir();
	father.remove(this);
    }

    public String getPath(){ return _path;}
    public Manager getManager(){ return _Manager;}
    public void setPath(String path){ _path=path;}

    public ArrayList<String> splitString(String s){
	ArrayList<String> pieces = new ArrayList<String>();
	pieces = Arrays.asList(s.split("/"));
	return pieces;
    }

    public File getFileFromPath(String path){
	ArrayList<String> pieces = splitString(path);
        File currentFile= _manager.getRootDirectory();
        for(String currentPiece: pieces)
		//TODO try catch isNotDirectoryException
            currentFile = currentDirectory.getFile(currentPiece);
        return currentFile;
    }
}
