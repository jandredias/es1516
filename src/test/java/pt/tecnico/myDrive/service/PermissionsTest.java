package pt.tecnico.myDrive.service;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.TestSetupException;

public abstract class PermissionsTest extends TokenAccessTest{
	
	protected abstract void populate(); 

	protected abstract MyDriveService createService(long token, String nameOfFileItOPerates);
	
	/**
	 * Method that is meant to be define by derivated classes
	 * returns the relevant char (r,w,x or d) to the service
	 */
	protected abstract String getPermissionString();
	
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
			int position = getPermissionString().indexOf(c);
			if (position == -1){
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
	private void setUpPermissionsTest(String permissions, String userBeingTested){
		
		permissions = restrictPermissions(permissions);
		String username			= "PermissionsUser"; 
		String folder 			= "PermissionsTestFolder";
		String testBaseFolder 	= "/home/" + username + "/" + folder;
		MyDrive md = MyDriveService.getMyDrive();
		try{
			md.addUser(username,username,username,"rwxdrwxd");
			User user = md.getUserByUsername(username);
			md.addDirectory("/home/" + username, folder, user);
			md.addApplication(testBaseFolder, "testedFile", user, "irrelevant");
			md.addDirectory(testBaseFolder, "changeDir",user);
//			md.addApplication(testBaseFolder, "App",user,"pt.tecnico.myDrive.presentation.");
			md.getFile(testBaseFolder + "/testedFile").setPermissions(permissions);
			md.getFile(testBaseFolder + "/changeDir" ).setPermissions(permissions);
			md.getFile(testBaseFolder).setPermissions(permissions);
		} catch(MyDriveException E){
			throw new TestSetupException("buliding permissions test");
		}
		
		if(userBeingTested.equals("OWNER"))
			userBeingTested = username;
		
		long token = md.getValidToken(userBeingTested,testBaseFolder, new StrictlyTestObject());
		abstractClassService = createService(token, "testedFile");
	}
	/* ********************************************************************** */
	/* ********************************************************************** */
	/* ********************************************************************** */
	@Test
	public void ownUserHasPermissions() throws MyDriveException{
		setUpPermissionsTest("rwxd----","OWNER");
		abstractClassService.execute();
		assertServiceExecutedWithSuccess();
	}

	@Test
	public void otherUserHasPermissions() throws MyDriveException{
		MyDrive md = MyDriveService.getMyDrive();
		String username = "otherPermissionUser";
		md.addUser(username,username,username,"--------");
		setUpPermissionsTest("----rwxd","otherPermissionUser");		
		abstractClassService.execute();
		assertServiceExecutedWithSuccess();
//		assertNotNull(service.result());
	}

	@Test(expected = PermissionDeniedException.class)
	public void ownUserHasNoPermissions() throws MyDriveException{
		setUpPermissionsTest("----rwxd","OWNER");
		abstractClassService.execute();
	}

	@Test(expected = PermissionDeniedException.class)
	public void otherUserHasNoPermissions() throws MyDriveException{
		MyDrive md = MyDriveService.getMyDrive();
		String username = "otherPermissionUser";
		md.addUser(username,username,username,"----rwxd");
		setUpPermissionsTest("rwxd----","otherPermissionUser");
		abstractClassService.execute();
	}

	@Test
	public void rootUserHasNoPermissions() throws MyDriveException{
		setUpPermissionsTest("--------","root");
		abstractClassService.execute();
		assertServiceExecutedWithSuccess();
	}
}