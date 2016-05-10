package pt.tecnico.myDrive.system.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import mockit.internal.expectations.TestOnlyPhase;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.MyDriveApplication;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.TestSetupException;
import pt.tecnico.myDrive.service.ChangeDirectoryService;
import pt.tecnico.myDrive.service.CreateFileService;
import pt.tecnico.myDrive.service.DeleteFileService;
import pt.tecnico.myDrive.service.ExecuteFileService;
import pt.tecnico.myDrive.service.ListDirectoryService;
import pt.tecnico.myDrive.service.LoginUserService;
import pt.tecnico.myDrive.service.ReadFileService;
import pt.tecnico.myDrive.service.TestClass;
import pt.tecnico.myDrive.service.WriteFileService;

@RunWith(JMockit.class)
public class SystemIntegrationTest {

	private final static String[] args = {};
	
	private MyDrive md;

	@Mocked
	private TestClass testClass;

	@Before
	public void setUp() {
		MyDriveApplication.init();
		try {
			FenixFramework.getTransactionManager().begin(false);
			md = MyDrive.getInstance();
			md.addUser("testuser", "bigpassword", "testuser", "rwxdrwxd");
		} catch (Exception e) {
			throw new TestSetupException("Failed integration test setup");
		}

		new Expectations() {
			{
				testClass.main(args);
			}
		};
	}

	@Test
	public void integrationTest() throws MyDriveException {
		long token;

		LoginUserService loginService = new LoginUserService("testuser", "bigpassword");
		loginService.execute();
		token = loginService.result();
		assertNotEquals(0, token);

		// TODO import xml service

		new CreateFileService(token, "mydir", "dir", "").execute();

		ListDirectoryService lsService = new ListDirectoryService(token);
		lsService.execute();
		printDirectory(lsService.result());

		ChangeDirectoryService cdService = new ChangeDirectoryService(token, "mydir");
		cdService.execute();
		assertEquals("/home/testuser/mydir", cdService.result());

		lsService.execute();
		printDirectory(lsService.result());

		new CreateFileService(token, "myfile.txt", "plainfile", "qwerty").execute();

		ReadFileService readPlainFile = new ReadFileService(token, "myfile.txt");
		readPlainFile.execute();
		assertEquals("qwerty", readPlainFile.result());

		new WriteFileService(token, "myfile.txt", "pt.tecnico.myDrive.service.TestClass").execute();

		readPlainFile.execute();
		assertEquals("pt.tecnico.myDrive.service.TestClass", readPlainFile.result());

		md.getFile("/home/testuser/mydir/myfile.txt").setPermissions("rwxdrwxd");

		ExecuteFileService execPlainFile = new ExecuteFileService(token, "myfile.txt", args);
		execPlainFile.execute();

		DeleteFileService delPlainFile = new DeleteFileService(token, "myfile.txt");
		delPlainFile.execute();

	}

	@After
	public void tearDown() {
		try {
			FenixFramework.getTransactionManager().rollback();
		} catch (Exception e) {
			throw new TestSetupException("Failed integration test tear down");
		}
	}

	private void printDirectory(List<List<String>> result) {
		System.out.println("DIRECTORY LISTING");
		for (List<String> value : result) {
			for (String par : value) {
				System.out.print(par + " ");
			}
			System.out.println();
		}
	}

}
