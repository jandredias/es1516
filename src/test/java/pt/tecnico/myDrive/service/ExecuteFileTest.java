package pt.tecnico.myDrive.service;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.TestSetupException;

@RunWith(JMockit.class)
public class ExecuteFileTest extends PermissionsTest {

	private MyDrive myDrive;
	private User owner;
	private long token;

	private final static String[] NOARGS = {};
	private final static String[] THEARGS = { "arg1", "arg2" };
	private static String[] expectedArgs;

	@Mocked
	private TestClass tc;

	protected void populate() {
		try {
			myDrive = MyDrive.getInstance();
			myDrive.addUser("andre", "andreandre", "André Dias", "rwxdrwxd");
			owner = myDrive.getUserByUsername("andre");
			token = myDrive.getValidToken("andre", "/home/andre", new StrictlyTestObject());

			myDrive.addApplication("/home/andre", "app", owner, "pt.tecnico.myDrive.service.TestClass");
			myDrive.addPlainFile("/home/andre", "fileNoArgs.txt", owner, "app");
			myDrive.addPlainFile("/home/andre", "file2Args.txt", owner, "app arg1 arg2");

		} catch (MyDriveException e) {
			throw new TestSetupException("ExecuteFileTest: populate");
		}
		expectedArgs = null;
	}

	@Override
	protected MyDriveService createService(long token, String filename) {
		return new ExecuteFileService(token, filename, new String[0]);
	}

	@Override
	protected String getPermissionString() {
		return "x";
	}

	@Override
	protected void assertServiceExecutedWithSuccess() {
		// we're only going to check the args if the service was executed
		// successfully, i.e., no exceptions were thrown
		if (expectedArgs != null) {
			new Verifications() {
				{
					tc.main(expectedArgs);
				}
			};
		}
	}

	@Test
	public void executeAppTestNoArgs() throws MyDriveException {
		ExecuteFileService service = new ExecuteFileService(token, "/home/andre/app", NOARGS);
		service.execute();

		expectedArgs = NOARGS;
		assertServiceExecutedWithSuccess();
	}

	@Test(expected = PermissionDeniedException.class)
	public void executeAppTestNoArgsNoPermissions() throws MyDriveException {
		myDrive.getFile("/home/andre/app").setPermissions("rw-drw-d");
		ExecuteFileService service = new ExecuteFileService(token, "/home/andre/app", NOARGS);
		service.execute();
	}

	@Test
	public void executeAppTestArgs() throws MyDriveException {
		ExecuteFileService service = new ExecuteFileService(token, "/home/andre/app", THEARGS);
		service.execute();

		expectedArgs = THEARGS;
		assertServiceExecutedWithSuccess();
	}

	@Test
	public void executeFileAppTestArgs() throws MyDriveException {
		ExecuteFileService service = new ExecuteFileService(token, "/home/andre/file2Args.txt", NOARGS);
		service.execute();

		expectedArgs = THEARGS;
		assertServiceExecutedWithSuccess();
	}

	@Test
	public void executeFileAppTestNoArgs() throws MyDriveException {
		ExecuteFileService service = new ExecuteFileService(token, "/home/andre/fileNoArgs.txt", NOARGS);
		service.execute();

		expectedArgs = NOARGS;
		assertServiceExecutedWithSuccess();
	}

}
