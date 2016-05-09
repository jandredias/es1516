package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.joda.time.DateTime;
import org.junit.Test;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PasswordTooShortException;
import pt.tecnico.myDrive.exception.TestSetupException;
import pt.tecnico.myDrive.exception.UserDoesNotExistsException;
import pt.tecnico.myDrive.exception.WrongPasswordException;

public class LoginUserTest extends AbstractServiceTest {

	protected void populate() {
		MyDrive mD = MyDriveService.getMyDrive();

		try {
			mD.addUser("Nuno", "123456789", null, null);
			mD.addUser("Abe", "asdfghjkl123", null, null);
			mD.addUser("Braga", "password", null, null);
			mD.addUser("Ricardo","benfica1904", null, null);
			mD.addUser("Xila", "qwertyuiop", null, null);
			mD.addUser("Joao", "987654321", null, null);
		}catch(MyDriveException e){
			throw new TestSetupException("LoginUserTest: Populate");
		}
	}
	
	@Test(expected = UserDoesNotExistsException.class)
	public void nullUsername() throws MyDriveException{
		LoginUserService service = new LoginUserService(null, "123456789");
		service.execute();
	}
	
	@Test(expected = WrongPasswordException.class)
	public void nullPassword() throws MyDriveException{
		LoginUserService service = new LoginUserService("Nuno", null);
		service.execute();
	}
	
	@Test(expected = PasswordTooShortException.class)
	public void passwordTooShort() throws MyDriveException{
		MyDrive mD = MyDriveService.getMyDrive();
		mD.addUser("Nuno68gt", "123456", null, null);
	}
	
	@Test(expected = InvalidUsernameException.class)
	public void emptyUsername() throws MyDriveException{
		MyDrive mD = MyDriveService.getMyDrive();
		mD.addUser("", "123456789", null, null);
	}
	
	@Test(expected = InvalidUsernameException.class)
	public void usernameTooShort() throws MyDriveException{
		MyDrive mD = MyDriveService.getMyDrive();
		mD.addUser("Ab", "123456789", null, null);
	}
	
	@Test(expected = InvalidUsernameException.class)
	public void usernameWithInvalidSymbols() throws MyDriveException{
		MyDrive mD = MyDriveService.getMyDrive();
		mD.addUser("Zezinho*", "123456789", null, null);
	}
	
	@Test(expected = UserDoesNotExistsException.class)
	public void nonExistingUsername() throws MyDriveException{
		LoginUserService service = new LoginUserService("Nuninho", "123456789");
		service.execute();
	}
	
	@Test(expected = WrongPasswordException.class)
	public void invalidPasswordForUser() throws MyDriveException{
		LoginUserService service = new LoginUserService("Nuno", "123467890");
		service.execute();
	}
	
	@Test
	public void sucess() throws MyDriveException{
		LoginUserService service = new LoginUserService("Nuno", "123456789");
		service.execute();
		long token = service.result();
		
		Session s = MyDrive.getInstance().getSessionByToken(token);
		
		assertNotNull("Session was not created", s);
		
		User user = s.getUser();
		String username = user.getUsername();
		Directory d = user.getUsersHome();
		
        assertEquals("Invalid session username", "Nuno", username);
        assertEquals("Invalid session current directory", d.getPath(), s.getCurrentDirectory().getPath());
	}
	
	@Test
	public void sucessWithUsernameSize3() throws MyDriveException{
		LoginUserService service = new LoginUserService("Abe", "asdfghjkl123");
		service.execute();
		long token = service.result();
		
		Session s = MyDrive.getInstance().getSessionByToken(token);
		
		assertNotNull("Session was not created", s);
		
		User user = s.getUser();
		String username = user.getUsername();
		Directory d = user.getUsersHome();
        assertEquals("Invalid session username", "Abe", username);
        assertEquals("Invalid session current directory", d.getPath(), s.getCurrentDirectory().getPath());
	}
	
	@Test
	public void sucessWithPasswordSize8() throws MyDriveException{
		LoginUserService service = new LoginUserService("Braga", "password");
		service.execute();
		long token = service.result();
		
		Session s = MyDrive.getInstance().getSessionByToken(token);
		
		assertNotNull("Session was not created", s);
		
		User user = s.getUser();
		String username = user.getUsername();
		Directory d = user.getUsersHome();
        assertEquals("Invalid session username", "Braga", username);
        assertEquals("Invalid session current directory", d.getPath(), s.getCurrentDirectory().getPath());
	}
	
	@Test
	public void sucessWithInvalidSessionsDeleted() throws MyDriveException{
		//Create expired session ("Ricardo","benfica1904")
		LoginUserService serv = new LoginUserService("Ricardo","benfica1904");
		serv.execute();
		long token = serv.result();
		
		MyDrive mD = MyDrive.getInstance();
		
		Session s = mD.getSessionByToken(token);
		DateTime time = new DateTime();
		time = time.minusHours(2);
		s.setLastUsed(time);
		
		LoginUserService service = new LoginUserService("Xila", "qwertyuiop");
		service.execute();
		
		//Confirm if the invalid session was deleted
		assertNull("Invalid session was not deleted", mD.getSessionByToken(token));
	}
	
	@Test
	public void sucessWithTwoSessionsSameUser() throws MyDriveException{
		LoginUserService serv = new LoginUserService("Joao", "987654321");
		serv.execute();
		
		LoginUserService service = new LoginUserService("Joao", "987654321");
		service.execute();
		long token = service.result();
		
		Session s = MyDrive.getInstance().getSessionByToken(token);
		
		assertNotNull("Session was not created", s);
		
		User user = s.getUser();
		String username = user.getUsername();
		Directory d = user.getUsersHome();
		
        assertEquals("Invalid session username", "Joao", username);
        assertEquals("Invalid session current directory", d.getPath(), s.getCurrentDirectory().getPath());

        //Check if the other session is still valid
        assertEquals("Deleted previous session", 2, user.getSessionSet().size());
	}
	
	@Test(expected = PasswordTooShortException.class)
	public void loginWithSmallPass() throws MyDriveException{
		LoginUserService service = new LoginUserService("Nuno", "1");
		service.execute();
	}
	
}