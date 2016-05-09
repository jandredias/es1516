package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.TestSetupException;

public class ExecuteFileTest extends PermissionsTest {

	private MyDrive myDrive;
	private User owner;
	private long token;

	private final static String[] noArgs = {};
	private final static String[] theArgs = {"arg1", "arg2"};

	protected void populate() {
		try {
			myDrive = MyDrive.getInstance();
			myDrive.addUser("andre", "andreandre", "André Dias", "rwxdrwxd");
			owner = myDrive.getUserByUsername("andre");
			token = myDrive.getValidToken("andre", "/home/andre", new StrictlyTestObject());

			myDrive.addApplication("/home/andre", "app", owner, "pt.tecnico.myDrive.service.TestClass");
			myDrive.addPlainFile("/home/andre", "fileNoArgs.txt", owner, "app");
			myDrive.addPlainFile("/home/andre", "file2Args.txt", owner,	"app arg1 arg2");

		} catch (MyDriveException e) {
			throw new TestSetupException("ExecuteFileTest: populate");
		}
		TestClass.resetClass();
	}

	@Override
	protected MyDriveService createService(long token, String filename) {
		return new ExecuteFileService(token, "/home/andre/file.txt", new String[0]);
	}

	@Override
	protected String getPermissionString() {
		return "x";
	}

	@Override
	protected void assertServiceExecutedWithSuccess() {
	}

	@Test
	public void executeAppTestNoArgs() throws MyDriveException {
		ExecuteFileService service = new ExecuteFileService(token, "/home/andre/app", noArgs);
		service.execute();

		assertEquals(0, TestClass.getArgsNum());
		assertTrue(TestClass.getRan());
	}

	@Test(expected = PermissionDeniedException.class)
	public void executeAppTestNoArgsNoPermissions() throws MyDriveException {
		myDrive.getFile("/home/andre/app").setPermissions("rw-drw-d");
		ExecuteFileService service = new ExecuteFileService(token, "/home/andre/app", noArgs);
		service.execute();
	}

	@Test
	public void executeAppTestArgs() throws MyDriveException {
		ExecuteFileService service = new ExecuteFileService(token, "/home/andre/app", theArgs);
		service.execute();

		assertTrue(TestClass.getRan());
		assertEquals(2, TestClass.getArgsNum());
		assertArguments(theArgs);
	}

	@Test
	public void executeFileAppTestArgs() throws MyDriveException {
		ExecuteFileService service = new ExecuteFileService(token, "/home/andre/file2Args.txt", noArgs);
		service.execute();
		
		assertTrue(TestClass.getRan());
		assertEquals(2, TestClass.getArgsNum());
		assertArguments(theArgs);
	}

	@Test
	public void executeFileAppTestNoArgs() throws MyDriveException {
		ExecuteFileService service = new ExecuteFileService(token, "/home/andre/fileNoArgs.txt", noArgs);
		service.execute();
		
		assertTrue(TestClass.getRan());
		assertEquals(0, TestClass.getArgsNum());
	}

	private void assertArguments(String[] expectedArgs) {
		Set<String> actualArgs = TestClass.getInvocationArgs();
		for (String expectedArg : expectedArgs) {
			boolean checked = false;
			for (String actualArg : actualArgs) {
				if (actualArg.equals(expectedArg)) {
					checked = true;
					break;
				}
			}
			assertTrue(checked);
		}
	}
}
