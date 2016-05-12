package pt.tecnico.myDrive.system.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import mockit.Mock;
import mockit.MockUp;
import pt.tecnico.myDrive.domain.Application;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.TestSetupException;
import pt.tecnico.myDrive.service.AbstractServiceTest;
import pt.tecnico.myDrive.service.AddVariableService;
import pt.tecnico.myDrive.service.ChangeDirectoryService;
import pt.tecnico.myDrive.service.CreateFileService;
import pt.tecnico.myDrive.service.DeleteFileService;
import pt.tecnico.myDrive.service.ExecuteFileService;
import pt.tecnico.myDrive.service.ImportXMLService;
import pt.tecnico.myDrive.service.ListDirectoryService;
import pt.tecnico.myDrive.service.LoginUserService;
import pt.tecnico.myDrive.service.ReadFileService;
import pt.tecnico.myDrive.service.TestClass;
import pt.tecnico.myDrive.service.WriteFileService;
import pt.tecnico.myDrive.service.dto.VariableDto;

public class SystemIntegrationTest extends AbstractServiceTest {

	private MyDrive md;

	private long token;
	private boolean testRan = false;
	private final String[] theArgs = {"arg1", "arg2"};

	@Override
	protected void populate() {
		try {
			md = MyDrive.getInstance();
			md.addUser("testuser", "bigpassword", "testuser", "rwxdrwxd");
			log.trace("testuser created successfully");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Test setup failure");
			throw new TestSetupException("Failed integration test setup");
		}
	}

	@Test
	public void integrationTest() throws MyDriveException {
		// this is for ExecuteFileService
		new MockUp<TestClass>() {
			@Mock
			public void main(String[] args) {
				testRan = true;
			}
		};
		log.trace("Mockup configured successfully");
		
		// login user
		LoginUserService loginService = new LoginUserService("testuser", "bigpassword");
		loginService.execute();
		token = loginService.result();
		assertNotEquals("Token was zero", 0, token);
		log.trace("LoginUserService executed successfully");
		
		// import xml
		Document doc = loadXMLDoc("drive.xml");
		assertNotNull("Document was null", doc);
		new ImportXMLService(doc).execute();
		User importedUser = md.getUserByUsername("teste");
		assertNotNull("User was null", importedUser);
		assertEquals("Names did not match", "testUser", importedUser.getName());
		assertEquals("Home dirs did not match", "/home/teste", importedUser.getUsersHome().getPath());
		log.trace("ImportXMLService executed successfully");
		
		// change directory
		ChangeDirectoryService cdService = new ChangeDirectoryService(token, "/home/testuser");
		cdService.execute();
		assertEquals("Home dirs did not match", "/home/testuser", cdService.result());
		log.trace("ChangeDirectoryService executed successfully");
		
		// create plain file
		new CreateFileService(token, "myfile.txt", "plainfile", "qwerty").execute();
		File myfile = md.getFile("/home/testuser/myfile.txt");
		assertTrue("myfile was not plainfile", myfile instanceof PlainFile);
		log.trace("CreateFileService (plain file) executed sucessfully");
		
		// create app
		new CreateFileService(token, "myapp", "app", "pt.tecnico.myDrive.service.TestClass").execute();
		File myapp= md.getFile("/home/testuser/myapp");
		assertTrue("myapp not app", myapp instanceof Application);
		log.trace("CreateFileService (app) executed successfully");
		
		// list dir
		ListDirectoryService lsService = new ListDirectoryService(token);
		lsService.execute();
		assertEquals("Directory did not have 4 files", 4, lsService.result().size()); // . .. myapp and myfile.txt
		log.trace("ListDirectoryService executed successfully");
		
		// change content of plain file
		new WriteFileService(token, "myfile.txt", "myapp").execute();
		
		// read plain file
		ReadFileService rService = new ReadFileService(token, "myfile.txt");
		rService.execute();
		assertEquals("File did not have correct content", "myapp", rService.result());
		log.trace("Write and ReadFileServices executed successfully");
		
		// execute plain file
		new ExecuteFileService(token, "myfile.txt", theArgs).execute();
		assertTrue("TestClass did not run", testRan);
		log.trace("ExecuteFileService executed successfully");
		
		// delete file
		DeleteFileService delService = new DeleteFileService(token, "myfile.txt");
		delService.execute();
		try {
			md.getFile("/home/testuser/myfile.txt");
			fail("File was not deleted");
		} catch (FileNotFoundException e) {
			// all ok
		}
		log.trace("DeleteFileService executed successfully");
		
		// add var
		AddVariableService varService = new AddVariableService(token, "myvar", "myvalue");
		varService.execute();
		Set<VariableDto> vars = varService.result();
		assertEquals("Unexpected number of defined variables", 1, vars.size());
		VariableDto var = vars.iterator().next();
		assertEquals("Variable name did not match", "myvar", var.getName());
		assertEquals("Variable value did not match", "myvalue", var.getValue());
		log.trace("AddVariableService executed successfully");
		
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
