package pt.tecnico.myDrive.service;

import org.joda.time.DateTime;
import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.TestSetupException;

public abstract class TokenAccessTest extends AbstractServiceTest{
	
	
	protected abstract void populate(); 

	protected abstract MyDriveService createService(long token, String nameOfFileItOPerates);
	protected abstract void assertServiceExecutedWithSuccess();

	protected MyDriveService abstractClassService;

	private void setUpTokenTest(long usedToken, boolean useInputToken){
	
		String username			= "TokenUser"; 
		String folder 			= "TokenTestFolder";
		String testBaseFolder 	= "/home/" + username + "/" + folder;
		MyDrive md = MyDriveService.getMyDrive();
		try{
			md.addUser(username,username,username,"rwxdrwxd");
			User user = md.getUserByUsername(username);
			md.addDirectory("/home/" + username, folder, user);
			md.addPlainFile(testBaseFolder, "testedFile", user, "irrelevant");
			md.addDirectory(testBaseFolder, "changeDir",user);
		} catch(MyDriveException E){
			throw new TestSetupException("buliding permissions test");
		}
		long token = md.getValidToken(username,testBaseFolder, new StrictlyTestObject());;
		if(useInputToken){
			if (token == usedToken) //Make sure the token we are going to use is invalid
				token++;
			else
				token = usedToken;
		}
		abstractClassService = createService(token,"testedFile");
	}
	
	 @Test(expected = InvalidTokenException.class)
	 public void TokenZero() throws MyDriveException{
		 setUpTokenTest(0,true);
		 abstractClassService.execute();
	 }
	
	 @Test
	 public void ValidToken() throws MyDriveException{
		 setUpTokenTest(0,false);
		 abstractClassService.execute();
		 assertServiceExecutedWithSuccess();
	 }
	
	 @Test(expected = InvalidTokenException.class)
	 public void InvalidNumberToken() throws MyDriveException{
		 setUpTokenTest(1,true);	
		 abstractClassService.execute();
	 }
	 
	 @Test(expected = InvalidTokenException.class)
	 public void ExpiredToken() throws MyDriveException{
		MyDrive md = MyDriveService.getMyDrive();
		setUpTokenTest(0,false);
		
		long token = md.getValidToken("TokenUser","/home/TokenUser/TokenTestFolder", new StrictlyTestObject());;
		Session session = md.getSessionByToken(token);
		session.setLastUsed(new DateTime().minusHours(2));
		
		
		abstractClassService = createService(token,"testedFile");
		abstractClassService.execute();
	 }
	 
//	 compiler does not acept null..	 
//	 @Test(expected = InvalidTokenException.class)
//	 public void InvalidNullToken() throws MyDriveException{
//		 setUpTokenTest(null,true);	
//		 abstractClassService.execute();
//	 }
}