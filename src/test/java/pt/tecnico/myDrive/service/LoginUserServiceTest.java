package pt.tecnico.myDrive.service;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.InvalidPasswordException;
import pt.tecnico.myDrive.exception.UserDoesNotExistsException;
import pt.tecnico.myDrive.exception.UsernameAlreadyInUseException;
import pt.tecnico.myDrive.exception.WrongPasswordException;

public class LoginUserServiceTest extends AbstractServiceTest {

	protected void populate() {
		MyDrive mD = MyDriveService.getMyDrive();

		try {
			mD.addUser("Nuno", "123456", null, null);
			mD.addUser("Abe", "asdfgh", null, null);
			mD.addUser("Ricardo","slb1904", null, null);
			mD.addUser("Xila", "qwerty", null, null);
			mD.addUser("Joao", "654321", null, null);
		} catch (InvalidUsernameException | UsernameAlreadyInUseException e) {
			assert false;
		}
	}
	
	@Test(expected = InvalidUsernameException.class)
	public void nullUsername() throws MyDriveException{
		LoginUserService service = new LoginUserService(null, "123456");
		service.execute();
	}
	
	@Test(expected = InvalidPasswordException.class)
	public void nullPassword() throws MyDriveException{
		LoginUserService service = new LoginUserService("Nuno", null);
		service.execute();
	}
	
	@Test(expected = InvalidUsernameException.class)
	public void emptyUsername() throws MyDriveException{
		LoginUserService service = new LoginUserService("", "123456");
		service.execute();
	}
	
	@Test(expected = InvalidUsernameException.class)
	public void usernameTooShort() throws MyDriveException{
		LoginUserService service = new LoginUserService("Ab", "123456");
		service.execute();
	}
	
	@Test(expected = InvalidUsernameException.class)
	public void usernameWithInvalidSymbols() throws MyDriveException{
		LoginUserService service = new LoginUserService("Ze*;", "123456");
		service.execute();
	}
	
	@Test(expected = UserDoesNotExistsException.class)
	public void nonExistingUsername() throws MyDriveException{
		LoginUserService service = new LoginUserService("Nuninho", "123456");
		service.execute();
	}
	
	@Test(expected = WrongPasswordException.class)
	public void invalidPasswordForUser() throws MyDriveException{
		LoginUserService service = new LoginUserService("Nuno", "12346");
		service.execute();
	}
	
	@Test
	public void sucess() throws MyDriveException{
		LoginUserService service = new LoginUserService("Nuno", "123456");
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
		LoginUserService service = new LoginUserService("Abe", "asdfgh");
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
	public void sucessWithInvalidSessionsDeleted() throws MyDriveException{
		//Create expired session ("Ricardo","slb1904")
		LoginUserService serv = new LoginUserService("Ricardo","slb1904");
		serv.execute();
		long token = serv.result();
		
		MyDrive mD = MyDrive.getInstance();
		
		Session s = mD.getSessionByToken(token);
		DateTime time = new DateTime();
		time = time.minusHours(2);
		s.setLastUsed(time);
		
		LoginUserService service = new LoginUserService("Xila", "qwerty");
		service.execute();
		
		//Confirm if the invalid session was deleted
		assertNull("Invalid session was not deleted", mD.getSessionByToken(token));
	}
	
	@Test
	public void sucessWithTwoSessionsSameUser() throws MyDriveException{
		LoginUserService serv = new LoginUserService("Joao", "654321");
		serv.execute();
		
		LoginUserService service = new LoginUserService("Joao", "654321");
		service.execute();
		long token = service.result();
		
		Session s = MyDrive.getInstance().getSessionByToken(token);
		
		assertNotNull("Session was not created", s);
		
		User user = s.getUser();
		String username = user.getUsername();
		Directory d = user.getUsersHome();
		
        assertEquals("Invalid session username", "Joao", username);
        assertEquals("Invalid session current directory", d.getPath(), s.getCurrentDirectory().getPath());

        //Check if the other session is still valid, need session method
        assertEquals("Deleted previous session", 2, user.getSessionSet().size());
	}
}