package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.TestSetupException;

public class CleanSessionServiceTest extends AbstractServiceTest {

	long token;
	MyDrive md;
	
	@Override
	protected void populate() {
		try {
			md = MyDriveService.getMyDrive();
			token = md.login("root", "***");
		} catch (MyDriveException e) {
			throw new TestSetupException("CleanSessionServiceTest: Populate");
		}
	}

	@Test
	public void removeValidSession() throws MyDriveException {
		MyDriveService service = new CleanSessionService(token);
		service.execute();
		assertNull(md.getSessionByToken(token));
	}

	@Test
	public void removeExpiredSession() throws MyDriveException {
		Session session = md.getSessionByToken(token);
		session.setLastUsed(new DateTime().minusDays(2000));
		MyDriveService service = new CleanSessionService(token);
		service.execute();
		assertNull(md.getSessionByToken(token));
	}
	
	@Test(expected = InvalidTokenException.class)
	public void inexitentTokenSession() throws MyDriveException {
		MyDriveService service = new CleanSessionService(0);
		service.execute();
	}

	@Test
	public void onlyRemoveReceivedToken() throws MyDriveException {
		long token1 = md.login("root", "***");
		long token2 = md.login("root", "***");
		long token3 = md.login("root", "***");
		long token4 = md.login("root", "***");

		MyDriveService service = new CleanSessionService(token);
		service.execute();
		
		assertTrue(token1!=token);
		assertTrue(token2!=token);
		assertTrue(token3!=token);
		assertTrue(token4!=token);
		
		assertNotNull(md.getSessionByToken(token1));
		assertNotNull(md.getSessionByToken(token2));
		assertNotNull(md.getSessionByToken(token3));
		assertNotNull(md.getSessionByToken(token4));
	}
}
