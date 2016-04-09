package pt.tecnico.myDrive.service;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

public abstract class PermissionsTest extends TokenAccessTest{
	
	private MyDriveService service;
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
	
	/* ********************************************************************** */
	/* ********************************************************************** */
	/* ********************************************************************** */
	private void setUpPermissionsTest(String permissions, String userBeingTested) 
				throws MyDriveException{
		
		permissions = restrictPermissions(permissions);
		String username			= "PermissionsUser"; 
		String folder 			= "PermissionsTestFolder";
		String testBaseFolder 	= "/home/" + username + "/" + folder;
		try{
			MyDrive md = MyDriveService.getMyDrive();
			md.addUser(username,username,username,permissions);
			User user = md.getUserByUsername(username);
			md.addDirectory("/home/" + username, folder, user);
			md.addPlainFile(testBaseFolder, "testedFile", user, "irrelevant");
		}
		catch(MyDriveException E){
			System.out.println("\u001B[31;1m"+"TEST ERROR" +" \u001B[0m");
			throw E;
		}
		
		if(userBeingTested.equals("OWNER"))
			userBeingTested = username;
		
		long token = 0;// = getValidToken(userBeingTested,testBaseFolder);
		service = createPermissionsService(token, "testedFile");
		System.out.println("\u001B[32;1m" + token +" \u001B[0m");
	}
	/* ********************************************************************** */
	/* ********************************************************************** */
	/* ********************************************************************** */
	@Test
	public void ownUserHasPermissions() throws MyDriveException{
		setUpPermissionsTest("rwxd----","OWNER");
		assert false;//FIXME:TODO:XXX
		service.execute();
		//assertNotNull(service.result());
	}

	@Test
	public void otherUserHasPermissions() throws MyDriveException{
		MyDrive md = MyDriveService.getMyDrive();
		md.addUser("otherPermissionUser","...","...","--------");
		setUpPermissionsTest("----rwxd","otherPermissionUser");		

		assert false;//FIXME:TODO:XXX
		service.execute();
//		assertNotNull(service.result());
	}

	@Test(expected = PermissionDeniedException.class)
	public void ownUserHasNoPermissions() throws MyDriveException{
		setUpPermissionsTest("----rwxd","OWNER");
		assert false;//FIXME:TODO:XXX
		service.execute();
	}

	@Test(expected = PermissionDeniedException.class)
	public void otherUserHasNoPermissions() throws MyDriveException{
		MyDrive md = MyDriveService.getMyDrive();
		md.addUser("otherPermissionUser","...","...","----rwxd");
		setUpPermissionsTest("rwxd----","otherPermissionUser");
		assert false;//FIXME:TODO:XXX
		service.execute();
	}

	@Test
	public void rootUserHasNoPermissions() throws MyDriveException{
		setUpPermissionsTest("--------","root");
		assert false;//FIXME:TODO:XXX
		service.execute();
	}
}