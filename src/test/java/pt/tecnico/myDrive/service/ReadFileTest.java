package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.TestSetupException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class ReadFileTest extends PermissionsTest {

	private MyDrive myDrive;
	private String myUsername, theirUsername;
	private User me, someone;
	private ReadFileService readFileService;

	private String myHomeDirPath = "/home/me";
	private String theirHomeDirPath = "/home/someone";

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
		token = MyDriveService.getMyDrive().getValidToken(myUsername, myHomeDirPath, new StrictlyTestObject());

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
		myDrive.addPlainFile(myHomeDirPath, "myFile.txt", me, "qwerty");
		myDrive.getFile(myHomeDirPath + "/myFile.txt").setPermissions("r-------");

		readFileService = new ReadFileService(token, "myFile.txt");
		readFileService.execute();
		assertEquals("qwerty", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOwnFileWithoutPermissionTest() throws MyDriveException {
		myDrive.addPlainFile(myHomeDirPath, "myFile.txt", me, "qwerty");
		myDrive.getFile(myHomeDirPath + "/myFile.txt").setPermissions("--------");

		readFileService = new ReadFileService(token, "myFile.txt");
		readFileService.execute();
	}

	@Test
	public void readOtherFileWithPermissionTest() throws MyDriveException {
		myDrive.addPlainFile(theirHomeDirPath, "theirFile.txt", someone, "qwerty");
		myDrive.getFile(theirHomeDirPath + "/theirFile.txt").setPermissions("----r---");

		changeCurrentSessionDirectory(theirHomeDirPath);

		readFileService = new ReadFileService(token, "theirFile.txt");

		readFileService.execute();
		assertEquals("qwerty", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOtherFileWithoutPermissionTest() throws MyDriveException {
		myDrive.addPlainFile(theirHomeDirPath, "theirFile.txt", someone, "qwerty");
		myDrive.getFile(theirHomeDirPath + "/theirFile.txt").setPermissions("--------");

		changeCurrentSessionDirectory(theirHomeDirPath);

		readFileService = new ReadFileService(token, "theirFile.txt");

		readFileService.execute();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void readDirectoryTest() throws MyDriveException {
		myDrive.addDirectory(myHomeDirPath, "durr", me);

		readFileService = new ReadFileService(token, "durr");
		readFileService.execute();
	}

	@Test
	public void readOwnLinkWithPermissionTest() throws MyDriveException {
		myDrive.addPlainFile(myHomeDirPath, "myFile.txt", me, "qwerty");
		myDrive.getFile(myHomeDirPath + "/myFile.txt").setPermissions("r-------");
		myDrive.addLink(myHomeDirPath, "myLink", me, myHomeDirPath + "/myFile.txt");
		myDrive.getFile(myHomeDirPath + "/myLink").setPermissions("r-------");

		readFileService = new ReadFileService(token, "myLink");
		readFileService.execute();
		assertEquals("qwerty", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOwnLinkWithoutPermissionTest() throws MyDriveException {
		myDrive.addPlainFile(myHomeDirPath, "myFile.txt", me, "qwerty");
		myDrive.getFile(myHomeDirPath + "/myFile.txt").setPermissions("r-------");
		myDrive.addLink(myHomeDirPath, "myLink", me, myHomeDirPath + "/myFile.txt");
		myDrive.getFile(myHomeDirPath + "/myLink").setPermissions("--------");

		readFileService = new ReadFileService(token, "myLink");
		readFileService.execute();
	}

	@Test
	public void readOtherLinkWithPermissionTest() throws MyDriveException {
		myDrive.addPlainFile(myHomeDirPath, "myFile.txt", me, "qwerty");
		myDrive.getFile(myHomeDirPath + "/myFile.txt").setPermissions("r-------");
		myDrive.addLink(theirHomeDirPath, "theirLink", someone, myHomeDirPath + "/myFile.txt");
		myDrive.getFile(theirHomeDirPath + "/theirLink").setPermissions("----r---");

		changeCurrentSessionDirectory(theirHomeDirPath);

		readFileService = new ReadFileService(token, "theirLink");
		readFileService.execute();
		assertEquals("qwerty", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOtherLinkWithoutPermissionTest() throws MyDriveException {
		myDrive.addPlainFile(myHomeDirPath, "myFile.txt", me, "qwerty");
		myDrive.getFile(myHomeDirPath + "/myFile.txt").setPermissions("r-------");
		myDrive.addLink(theirHomeDirPath, "theirLink", someone, myHomeDirPath + "myFile.txt");
		myDrive.getFile(theirHomeDirPath + "/theirLink").setPermissions("--------");

		changeCurrentSessionDirectory(theirHomeDirPath);

		readFileService = new ReadFileService(token, "theirLink");
		readFileService.execute();
	}

	@Test
	public void readOwnAppWithPermissionTest() throws MyDriveException {
		myDrive.addApplication(myHomeDirPath, "application.apk", me, "java.lang.NullPointerException");
		myDrive.getFile(myHomeDirPath + "/application.apk").setPermissions("r-x-----");

		readFileService = new ReadFileService(token, "application.apk");
		readFileService.execute();
		assertEquals("java.lang.NullPointerException", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOwnAppWithoutPermissionTest() throws MyDriveException {
		myDrive.addApplication(myHomeDirPath, "application.apk", me, "java.lang.NullPointerException");
		myDrive.getFile(myHomeDirPath + "/application.apk").setPermissions("--x-----");

		readFileService = new ReadFileService(token, "application.apk");
		readFileService.execute();
	}

	@Test
	public void readOtherAppWithPermissionTest() throws MyDriveException {
		myDrive.addApplication(theirHomeDirPath, "application.apk", someone, "java.lang.NullPointerException");
		myDrive.getFile(theirHomeDirPath + "/application.apk").setPermissions("r-x-r-x-");

		changeCurrentSessionDirectory(theirHomeDirPath);

		readFileService = new ReadFileService(token, "application.apk");
		readFileService.execute();
		assertEquals("java.lang.NullPointerException", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOtherAppWithoutPermissionTest() throws MyDriveException {
		myDrive.addApplication(theirHomeDirPath, "application.apk", someone, "java.lang.NullPointerException");
		myDrive.getFile(theirHomeDirPath + "/application.apk").setPermissions("--x-----");

		changeCurrentSessionDirectory(theirHomeDirPath);

		readFileService = new ReadFileService(token, "application.apk");
		readFileService.execute();
	}

	private void changeCurrentSessionDirectory(String directory) throws FileNotFoundException, NotDirectoryException {
		myDrive.getSessionByToken(token).setCurrentDirectory(myDrive.getDirectory(directory));
	}

}
