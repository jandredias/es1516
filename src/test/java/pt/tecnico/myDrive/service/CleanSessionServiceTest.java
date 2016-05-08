package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertNull;

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

}
