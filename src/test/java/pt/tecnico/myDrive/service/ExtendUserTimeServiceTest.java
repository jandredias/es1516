package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.TestSetupException;

public class ExtendUserTimeServiceTest extends AbstractServiceTest {

	private MyDrive myDrive;
	private long token;
	private User user;
	private Session session;
	
	private ExtendUserTimeService extendService;
	
	@Override
	protected void populate() {
		try {
			myDrive = MyDrive.getInstance();
			myDrive.addUser("john", "qwerty1234", "John", null);
			token = myDrive.getNewToken();
			user = myDrive.getUserByUsername("john");
			session  = new Session(user, token);
			assertEquals(session, myDrive.getSessionByToken(token));
		} catch (MyDriveException e) {
			throw new TestSetupException("ExtendUserTimeService: Populate");
		}
	}

	@Test
	public void almostExpiredSessionTest() throws MyDriveException {
		DateTime oneHourAgo = DateTime.now().minusHours(1);
		session.setLastUsed(oneHourAgo);
		
		extendService = new ExtendUserTimeService(token);
		extendService.execute();
		
		DateTime lastUsed = session.getLastUsed();
		
		boolean sessionExtended = lastUsed.minus(oneHourAgo.getMillis()).getMillis() > 0;
		assertTrue(sessionExtended);
	}
	
	@Test(expected = InvalidTokenException.class)
	public void expiredSessionTest() throws MyDriveException {
		DateTime threeHoursAgo = DateTime.now().minusHours(3);
		session.setLastUsed(threeHoursAgo);
		
		extendService = new ExtendUserTimeService(token);
		extendService.execute();
	}

}
