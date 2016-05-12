package pt.tecnico.myDrive.system.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.TestSetupException;
import pt.tecnico.myDrive.service.AbstractServiceTest;
import pt.tecnico.myDrive.service.ChangeDirectoryService;
import pt.tecnico.myDrive.service.CreateFileService;
import pt.tecnico.myDrive.service.ImportXMLService;
import pt.tecnico.myDrive.service.ListDirectoryService;
import pt.tecnico.myDrive.service.LoginUserService;

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

		Document doc = loadXMLDoc("drive.xml");
		assertNotNull(doc);
		ImportXMLService xmlService = new ImportXMLService(doc);
		xmlService.execute();
		
		ChangeDirectoryService cdService = new ChangeDirectoryService(token, "/home/testuser");
		cdService.execute();
		assertEquals("/home/testuser", cdService.result());
		
		CreateFileService touchService = new CreateFileService(token, "myfile.txt", "plainfile", "qwerty");
		touchService.execute();
		File myfile = md.getFile("/home/testuser/myfile.txt");
		assertTrue(myfile instanceof PlainFile);
		
		ListDirectoryService lsService = new ListDirectoryService(token);
		lsService.execute();
		assertEquals(3, lsService.result().size()); // . .. and myfile.txt
		
		fail("Not yet complete");
	}

	private Document loadXMLDoc(String path) {
		Document doc = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			java.io.File file;
			if (path.startsWith("."))
				file = new java.io.File(path);
			else
				file = resourceFile(path);
			doc = (Document) builder.build(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	public java.io.File resourceFile(String filename) {
		log.trace("Resource: " + filename);
		ClassLoader classLoader = getClass().getClassLoader();
		if (classLoader.getResource(filename) == null)
			return null;
		return new java.io.File(classLoader.getResource(filename).getFile());
	}

}
