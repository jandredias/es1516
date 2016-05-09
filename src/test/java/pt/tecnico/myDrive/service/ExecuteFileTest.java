package pt.tecnico.myDrive.service;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidAppContentException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.TestSetupException;
import pt.tecnico.myDrive.exception.UsernameAlreadyInUseException;

public class ExecuteFileTest extends PermissionsTest{

	protected void populate(){
		try {
			MyDrive.getInstance().addUser("andre", "andreandre", "André Dias", "rwxdrwxd");
		} catch (InvalidUsernameException | UsernameAlreadyInUseException e) {
			throw new TestSetupException("ExecuteFileTest: populate");
		}
	}

	@Override
	protected MyDriveService createService(long token, String filename) {
		return new ExecuteFileService(token, "/home/PermissionsUser/PermissionsTestFolder/App", new String[0]);
	}

	@Override
	protected String getPermissionString() {
		return "x";
	}

	@Override
	protected void assertServiceExecutedWithSuccess() {
		<>
	}

	
	@Test
	public void executeAppTestNoArgs() throws MyDriveException {
		//TODO
	}
	
	//@Test(expected = PermissionDeniedException.class)
	public void executeAppTestNoArgsNoPermissions() throws MyDriveException {
		//TODO
	}

	@Test
	public void executeAppTestArgs() throws MyDriveException {
		//TODO
	}
	
	@Test
	public void executeFileAppTestArgs() throws MyDriveException {
		//TODO
	}
}
