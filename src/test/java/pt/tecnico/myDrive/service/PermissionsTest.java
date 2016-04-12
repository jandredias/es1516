package pt.tecnico.myDrive.service;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.TestSetupException;

public abstract class PermissionsTest extends TokenAccessTest{
	
	protected MyDriveService permissionsService;
	protected abstract void populate(); 

	protected abstract MyDriveService createTokenService(long token);
	
	protected abstract MyDriveService createPermissionsService(long token, String nameOfFileItOPerates);
	
	/**
	 * Method that is meant to be define by derivated classes
	 * returns the relevant char (r,w,x or d) to the service
	 */
	protected abstract char getPermissionChar();
	
	/**
	 * Method that goes through every char of the string and keep 
	 * only the service relavant ones
	 * 
	 * @param  permissions
	 * @return permissions 
	 */
	private String restrictPermissions(String permissions){
		for (int i = 0; i < permissions.length(); i++){
			char c = permissions.charAt(i);
			if (c != getPermissionChar()){
				permissions = permissions.substring(0, i) + "-" + permissions.substring(i+1);
			}
		}
		return permissions;
	}
	
	/**
	 * Method that every subclass redefines and ensures a service has run
	 */
	protected abstract void assertServiceExecutedWithSuccess();
	
	/* ********************************************************************** */
	/* ********************************************************************** */
	/* ********************************************************************** */
	private void setUpPermissionsTest(String permissions, String userBeingTested) 
				throws MyDriveException{
		
		permissions = restrictPermissions(permissions);
		String username			= "PermissionsUser"; 
		String folder 			= "PermissionsTestFolder";
		String testBaseFolder 	= "/home/" + username + "/" + folder;
		MyDrive md = MyDriveService.getMyDrive();
		try{
			md.addUser(username,username,username,"rwxdrwxd");
			User user = md.getUserByUsername(username);
			md.addDirectory("/home/" + username, folder, user);
			md.addPlainFile(testBaseFolder, "testedFile", user, "irrelevant");
			md.getFile("/home/" + username + "/" + folder + "/testedFile").setPermissions(permissions);
			md.getFile("/home/" + username + "/" + folder).setPermissions(permissions);
		} catch(MyDriveException E){
			throw new TestSetupException("buliding permissions test");
		}
		
		if(userBeingTested.equals("OWNER"))
			userBeingTested = username;
		
		long token = md.getValidToken(userBeingTested,testBaseFolder, new StrictlyTestObject());
		permissionsService = createPermissionsService(token, "testedFile");
		System.out.println("\u001B[32;1m" + token +" \u001B[0m");
	}
	/* ********************************************************************** */
	/* ********************************************************************** */
	/* ********************************************************************** */
	@Test
	public void ownUserHasPermissions() throws MyDriveException{
		setUpPermissionsTest("rwxd----","OWNER");
		permissionsService.execute();
		assertServiceExecutedWithSuccess();
	}

	@Test
	public void otherUserHasPermissions() throws MyDriveException{
		MyDrive md = MyDriveService.getMyDrive();
		md.addUser("otherPermissionUser","...","...","--------");
		setUpPermissionsTest("----rwxd","otherPermissionUser");		
		permissionsService.execute();
		assertServiceExecutedWithSuccess();
//		assertNotNull(service.result());
	}

	@Test(expected = PermissionDeniedException.class)
	public void ownUserHasNoPermissions() throws MyDriveException{
		setUpPermissionsTest("----rwxd","OWNER");
		permissionsService.execute();
	}

	@Test(expected = PermissionDeniedException.class)
	public void otherUserHasNoPermissions() throws MyDriveException{
		MyDrive md = MyDriveService.getMyDrive();
		md.addUser("otherPermissionUser","...","...","----rwxd");
		setUpPermissionsTest("rwxd----","otherPermissionUser");
		permissionsService.execute();
	}

	@Test
	public void rootUserHasNoPermissions() throws MyDriveException{
		setUpPermissionsTest("--------","root");
		permissionsService.execute();
		assertServiceExecutedWithSuccess();
	}
}