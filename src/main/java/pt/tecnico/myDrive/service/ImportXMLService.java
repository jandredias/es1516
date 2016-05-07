package pt.tecnico.myDrive.service;

import java.lang.reflect.InvocationTargetException;

import org.jdom2.Document;

import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.UserDoesNotExistsException;
import pt.tecnico.myDrive.exception.UsernameAlreadyInUseException;

public class ImportXMLService extends MyDriveService {

	private final Document _doc;

	public ImportXMLService(Document doc){
		this._doc=doc;
	}

	@Override
	public final void dispatch() throws ImportDocumentException{
		getMyDrive().xmlImport(_doc.getRootElement());
	}

	
}
