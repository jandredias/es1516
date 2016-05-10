package pt.tecnico.myDrive.system.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.MyDriveApplication;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.TestSetupException;
import pt.tecnico.myDrive.service.ChangeDirectoryService;
import pt.tecnico.myDrive.service.CreateFileService;
import pt.tecnico.myDrive.service.DeleteFileService;
import pt.tecnico.myDrive.service.ExecuteFileService;
import pt.tecnico.myDrive.service.LoginUserService;
import pt.tecnico.myDrive.service.ReadFileService;
import pt.tecnico.myDrive.service.WriteFileService;

public class SystemIntegrationTest {

	private MyDrive md;

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
	}

	@Test
	public void integrationTest() throws MyDriveException {
		long token;
		
		LoginUserService loginService = new LoginUserService("testuser", "bigpassword");
		loginService.execute();
		token = loginService.result();
		assertNotEquals(0, token);
		
		// TODO import xml service
		
		CreateFileService createDir = new CreateFileService(token, "mydir", "dir", "");
		createDir.execute();
		
		ChangeDirectoryService cdService = new ChangeDirectoryService(token, "mydir");
		cdService.execute();
		assertEquals("/home/testuser/mydir", cdService.result());
		
		CreateFileService createPlainFile = new CreateFileService(token, "myfile.txt", "plainfile", "qwerty");
		createPlainFile.execute();
		
		ReadFileService readPlainFile = new ReadFileService(token, "myfile.txt");
		readPlainFile.execute();
		assertEquals("qwerty", readPlainFile.result());
		
		WriteFileService writePlainfile = new WriteFileService(token, "myfile.txt", "pt.tecnico.myDrive.service.TestClass");
		writePlainfile.execute();
		
		readPlainFile.execute();
		assertEquals("pt.tecnico.myDrive.service.TestClass", readPlainFile.result());
		
		String[] args = {};
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

}
