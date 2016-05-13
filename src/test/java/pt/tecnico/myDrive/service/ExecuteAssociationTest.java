package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;

import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.ExecuteFileVisitor;
import pt.tecnico.myDrive.domain.Extension;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.NoExtensionFoundException;
import pt.tecnico.myDrive.exception.TestSetupException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.service.ExecuteFileService;
import mockit.integration.junit4.JMockit;


@RunWith(JMockit.class)
public class ExecuteAssociationTest extends AbstractServiceTest {
	
	private MyDrive myDrive;
	private User owner;
	private long token;
	private final static String[] NOARGS = {};

	
	@Override
	protected void populate() {
		try {
			myDrive = MyDrive.getInstance();
			myDrive.addUser("andre", "andreandre", "André Dias", "rwxdrwxd");
			User u1 = myDrive.getUserByUsername("andre");
			token = myDrive.getValidToken("andre", "/home/andre", new StrictlyTestObject());

			myDrive.addLink("/home/andre", "link", u1, "/home/andre/file1");
			
			myDrive.addPlainFile("/home/andre", "file1", u1, "app");

			myDrive.addPlainFile("/home/andre", "file2", u1, "app arg1 arg2");

		} catch (MyDriveException e) {
			throw new TestSetupException("ExecuteAssociationTest: populate");
		}
	}
	

	@Test
	public void executeLink() throws Exception {	
		
		new MockUp<ExecuteFileVisitor>() {
			  @Mock
			  void visitLink(Link test){ }
			  @Mock
			  void visitPlainFile(PlainFile test){ }
		};		 


		ExecuteFileService service = new ExecuteFileService(token, "/home/andre/link", NOARGS);
		service.execute();

	}
	
	@Test(expected = NoExtensionFoundException.class)
	public void executeLinkNoAssoc() throws Exception {	
		
		new MockUp<ExecuteFileVisitor>() {
			  @Mock
			  void visitLink(Link test){ }
			  @Mock
			  void visitPlainFile(PlainFile test) throws MyDriveException { throw new NoExtensionFoundException(); }
		};		 

		ExecuteFileService service = new ExecuteFileService(token, "/home/andre/link", NOARGS);
		service.execute();

	}
	
	@Test(expected = NoExtensionFoundException.class)
	public void executePlainNoAssoc() throws Exception {	
		
		new MockUp<ExecuteFileVisitor>() {
			  @Mock
			  void visitPlainFile(PlainFile test) throws MyDriveException { throw new NoExtensionFoundException(); }
		};		 

		ExecuteFileService service = new ExecuteFileService(token, "/home/andre/file1", NOARGS);
		service.execute();

	}
	
	@Test
	public void executePlain() throws Exception {	
		
		new MockUp<ExecuteFileVisitor>() {
			  @Mock
			  void visitPlainFile(PlainFile test) { }
		};		 

		ExecuteFileService service = new ExecuteFileService(token, "/home/andre/file1", NOARGS);
		service.execute();

	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void executeDir() throws Exception {	
		
		new MockUp<ExecuteFileVisitor>() {
			  @Mock
			  void visitPlainFile(PlainFile test) { }
		};		 

		ExecuteFileService service = new ExecuteFileService(token, "/home/andre", NOARGS);
		service.execute();

	}
	
}
