package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.TestSetupException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class ReadFileTest extends PermissionsTest {

	private MyDrive myDrive;
	private String myUsername, theirUsername;
	private User me, someone;
	private ReadFileService readFileService;

	private long token = 0;

	protected void populate() {
		myDrive = MyDriveService.getMyDrive();

		myUsername = "me";
		theirUsername = "someone";
		
		try {
			myDrive.addUser("me", "qwerty123", "Jimmy", null);
			myDrive.addUser("someone", "qwerty123", "Sarah", null);
		} catch (MyDriveException e) {
			throw new TestSetupException("ReadFileTest failed on setup");
		}
		token = MyDriveService.getMyDrive().getValidToken(myUsername, "/home/" + myUsername, new StrictlyTestObject());

		me = myDrive.getUserByUsername(myUsername);
		someone = myDrive.getUserByUsername(theirUsername);
	}

	@Override
	protected MyDriveService createService(long token, String nameOfFileItOPerates) {
		return new ReadFileService(token, nameOfFileItOPerates);
	}

	@Override
	protected char getPermissionChar() {
		return 'r';
	}
	
	@Override
	protected void assertServiceExecutedWithSuccess() {
		readFileService = (ReadFileService) abstractClassService;
		assertNotNull(readFileService);
	}

	@Test
	public void readOwnFileWithPermissionTest() throws MyDriveException {
		myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
		myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");

		readFileService = new ReadFileService(token, "/home/me/myFile.txt");
		readFileService.execute();
		assertEquals("qwerty", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOwnFileWithoutPermissionTest() throws MyDriveException {
		myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
		myDrive.getFile("/home/me/myFile.txt").setPermissions("--------");

		readFileService = new ReadFileService(token, "/home/me/myFile.txt");
		readFileService.execute();
		// no asserts because PermissionDeniedException is expected
	}

	@Test
	public void readOtherFileWithPermissionTest() throws MyDriveException {
		myDrive.addPlainFile("/home/someone", "theirFile.txt", someone, "qwerty");
		myDrive.getFile("/home/someone/theirFile.txt").setPermissions("----r---");

		readFileService = new ReadFileService(token, "/home/someone/theirFile.txt");
		readFileService.execute();
		assertEquals("qwerty", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOtherFileWithoutPermissionTest() throws MyDriveException {
		myDrive.addPlainFile("/home/someone", "theirFile.txt", someone, "qwerty");
		myDrive.getFile("/home/someone/theirFile.txt").setPermissions("--------");

		readFileService = new ReadFileService(token, "/home/someone/theirFile.txt");
		readFileService.execute();
		// no asserts because PermissionDeniedException is expected
	}

	@Test(expected = UnsupportedOperationException.class)
	public void readDirectoryTest() throws MyDriveException {
		myDrive.addDirectory("/home/me", "durr", me);

		readFileService = new ReadFileService(token, "/home/me/durr");
		readFileService.execute();
		// no asserts because UnsupportedOperationException is expected
	}

	@Test
	public void readOwnLinkWithPermissionTest() throws MyDriveException {
		myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
		myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");
		myDrive.addLink("/home/me", "myLink", me, "/home/me/myFile.txt");
		myDrive.getFile("/home/me/myLink").setPermissions("r-------");

		readFileService = new ReadFileService(token, "/home/me/myLink");
		readFileService.execute();
		assertEquals("qwerty", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOwnLinkWithoutPermissionTest() throws MyDriveException {
		myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
		myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");
		myDrive.addLink("/home/me", "myLink", me, "/home/me/myFile.txt");
		myDrive.getFile("/home/me/myLink").setPermissions("--------");

		readFileService = new ReadFileService(token, "/home/me/myLink");
		readFileService.execute();
		// no asserts because PermissionDeniedException is expected
	}

	@Test
	public void readOtherLinkWithPermissionTest() throws MyDriveException {
		myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
		myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");
		myDrive.addLink("/home/someone", "theirLink", someone, "/home/me/myFile.txt");
		myDrive.getFile("/home/someone/theirLink").setPermissions("----r---");

		readFileService = new ReadFileService(token, "/home/someone/theirLink");
		readFileService.execute();
		assertEquals("qwerty", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOtherLinkWithoutPermissionTest() throws MyDriveException {
		myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
		myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");
		myDrive.addLink("/home/someone", "theirLink", someone, "/home/me/myFile.txt");
		myDrive.getFile("/home/someone/theirLink").setPermissions("--------");

		readFileService = new ReadFileService(token, "/home/someone/theirLink");
		readFileService.execute();
		// no asserts because PermissionDeniedException is expected
	}

	@Test
	public void readOwnAppWithPermissionTest() throws MyDriveException {
		myDrive.addApplication("/home/me", "application.apk", me, "java.lang.NullPointerException");
		myDrive.getFile("/home/me/application.apk").setPermissions("r-x-----");

		readFileService = new ReadFileService(token, "/home/me/application.apk");
		readFileService.execute();
		assertEquals("java.lang.NullPointerException", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOwnAppWithoutPermissionTest() throws MyDriveException {
		myDrive.addApplication("/home/me", "application.apk", me, "java.lang.NullPointerException");
		myDrive.getFile("/home/me/application.apk").setPermissions("--x-----");

		readFileService = new ReadFileService(token, "/home/me/application.apk");
		readFileService.execute();
		// no asserts because PermissionDeniedException is expected
	}

	@Test
	public void readOtherAppWithPermissionTest() throws MyDriveException {
		myDrive.addApplication("/home/someone", "application.apk", someone, "java.lang.NullPointerException");
		myDrive.getFile("/home/someone/application.apk").setPermissions("r-x-r-x-");

		readFileService = new ReadFileService(token, "/home/someone/application.apk");
		readFileService.execute();
		assertEquals("java.lang.NullPointerException", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOtherAppWithoutPermissionTest() throws MyDriveException {
		myDrive.addApplication("/home/someone", "application.apk", someone, "java.lang.NullPointerException");
		myDrive.getFile("/home/someone/application.apk").setPermissions("--x-----");

		readFileService = new ReadFileService(token, "/home/someone/application.apk");
		readFileService.execute();
		// no asserts because PermissionDeniedException is expected
	}

}
