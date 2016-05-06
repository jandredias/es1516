package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;

public class ExtendUserTimeServiceTest {

	private MyDrive myDrive;
	private User user;
	private long token;
	private Session session;
	
	private ExtendUserTimeService extendService;
	
	@Before
	public void setUp() throws Exception {
		myDrive = MyDrive.getInstance();
		myDrive.addUser("john", "qwerty1234", "John", null);
		user = myDrive.getUserByUsername("john");
		token = myDrive.getNewToken();
		session = myDrive.getSessionByToken(token);
	}

	@Test
	public void almostExpiredSessionTest() throws MyDriveException {
		DateTime oneHourAgo = DateTime.now().minusHours(1);
		session.setLastUsed(oneHourAgo);
		
		extendService = new ExtendUserTimeService(user);
		extendService.execute();
		
		DateTime lastUsed = session.getLastUsed();
		
		assertTrue(lastUsed.minus(oneHourAgo.getMillis()).getMillis() > 0);
	}
	
	@Test(expected = InvalidTokenException.class)
	public void expiredSessionTest() throws MyDriveException {
		DateTime threeHoursAgo = DateTime.now().minusHours(3);
		session.setLastUsed(threeHoursAgo);
		
		extendService = new ExtendUserTimeService(user);
		extendService.execute();
	}

}
