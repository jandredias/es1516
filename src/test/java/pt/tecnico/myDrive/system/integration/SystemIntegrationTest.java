package pt.tecnico.myDrive.system.integration;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.TestSetupException;
import pt.tecnico.myDrive.service.AbstractServiceTest;
import pt.tecnico.myDrive.service.LoginUserService;

@RunWith(JMockit.class)
public class SystemIntegrationTest extends AbstractServiceTest {

	private MyDrive md;
	
	private long token;

	@Override
	protected void populate() {
		try {
			md = MyDrive.getInstance();
			md.addUser("testuser", "bigpassword", "testuser", "rwxdrwxd");
		} catch (Exception e) {
			throw new TestSetupException("Failed integration test setup");
		}

	}

	@Test
	public void integrationTest() throws MyDriveException {
		// LoginUserService
		LoginUserService loginService = new LoginUserService("testuser", "bigpassword");
		loginService.execute();
		token = loginService.result();
		assertNotEquals(0, token);
		
		fail("Not yet complete");
	}

}
