package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.UsernameAlreadyInUseException;

public class ReadFileTest extends AbstractServiceTest {

	private MyDrive myDrive;
	private User me, someone;
	private ReadFileService readFileService;

	protected void populate() {
		myDrive = MyDriveService.getMyDrive();

		try {
			myDrive.addUser("me", "qwerty123", "Jimmy", null);
			myDrive.addUser("someone", "qwerty123", "Sarah", null);
		} catch (InvalidUsernameException | UsernameAlreadyInUseException e) {
			assert false;
		}

		me = myDrive.getUserByUsername("me");
		someone = myDrive.getUserByUsername("someone");
	}

	@Test
	public void readOwnFileWithPermissionTest() throws MyDriveException {
		try {
			myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
			myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}

		readFileService = new ReadFileService("/home/me/myFile.txt");
		readFileService.execute();
		assertEquals("qwerty", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOwnFileWithoutPermissionTest() throws MyDriveException {
		try {
			myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
			myDrive.getFile("/home/me/myFile.txt").setPermissions("--------");
		} catch (MyDriveException e) {
			fail("Should have not thrown exception");
		}

		readFileService = new ReadFileService("/home/me/myFile.txt");
		readFileService.execute();
		// no asserts because PermissionDeniedException is expected
	}

	@Test
	public void readOtherFileWithPermissionTest() throws MyDriveException {
		try {
			myDrive.addPlainFile("/home/someone", "theirFile.txt", someone, "qwerty");
			myDrive.getFile("/home/someone/theirFile.txt").setPermissions("----r---");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}

		readFileService = new ReadFileService("/home/someone/theirFile.txt");
		readFileService.execute();
		assertEquals("qwerty", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOtherFileWithoutPermissionTest() throws MyDriveException {
		try {
			myDrive.addPlainFile("/home/someone", "theirFile.txt", someone, "qwerty");
			myDrive.getFile("/home/someone/theirFile.txt").setPermissions("--------");
		} catch (MyDriveException e) {
			fail("Should have not thrown exception");
		}

		readFileService = new ReadFileService("/home/someone/theirFile.txt");
		readFileService.execute();
		// no asserts because PermissionDeniedException is expected
	}

	@Test(expected = UnsupportedOperationException.class)
	public void readDirectoryTest() throws MyDriveException {
		try {
			myDrive.addDirectory("/home/me", "durr", me);
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}

		readFileService = new ReadFileService("/home/me/durr");
		readFileService.execute();
		// no asserts because UnsupportedOperationException is expected
	}

	@Test
	public void readOwnLinkWithPermissionTest() throws MyDriveException {
		try {
			myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
			myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");
			myDrive.addLink("/home/me", "myLink", me, "/home/me/myFile.txt");
			myDrive.getFile("/home/me/myLink").setPermissions("r------l");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}

		readFileService = new ReadFileService("/home/me/myLink");
		readFileService.execute();
		assertEquals("qwerty", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOwnLinkWithoutPermissionTest() throws MyDriveException {
		try {
			myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
			myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");
			myDrive.addLink("/home/me", "myLink", me, "/home/me/myFile.txt");
			myDrive.getFile("/home/me/myLink").setPermissions("-------l");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}

		readFileService = new ReadFileService("/home/me/myLink");
		readFileService.execute();
		// no asserts because PermissionDeniedException is expected
	}

	@Test
	public void readOtherLinkWithPermissionTest() throws MyDriveException {
		try {
			myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
			myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");
			myDrive.addLink("/home/someone", "theirLink", someone, "/home/me/myFile.txt");
			myDrive.getFile("/home/someone/theirLink").setPermissions("----r--l");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}

		readFileService = new ReadFileService("/home/someone/theirLink");
		readFileService.execute();
		assertEquals("qwerty", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOtherLinkWithoutPermissionTest() throws MyDriveException {
		try {
			myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
			myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");
			myDrive.addLink("/home/someone", "theirLink", someone, "/home/me/myFile.txt");
			myDrive.getFile("/home/someone/theirLink").setPermissions("-------l");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}

		readFileService = new ReadFileService("/home/someone/theirLink");
		readFileService.execute();
		// no asserts because PermissionDeniedException is expected
	}

	@Test
	public void readOwnAppWithPermissionTest() throws MyDriveException {
		try {
			myDrive.addApplication("/home/me", "application.apk", me, "java.lang.NullPointerException");
			myDrive.getFile("/home/me/application.apk").setPermissions("r-x-----");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}

		readFileService = new ReadFileService("/home/me/application.apk");
		readFileService.execute();
		assertEquals("java.lang.NullPointerException", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOwnAppWithoutPermissionTest() throws MyDriveException {
		try {
			myDrive.addApplication("/home/me", "application.apk", me, "java.lang.NullPointerException");
			myDrive.getFile("/home/me/application.apk").setPermissions("--x-----");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}

		readFileService = new ReadFileService("/home/me/application.apk");
		readFileService.execute();
		// no asserts because PermissionDeniedException is expected
	}

	@Test
	public void readOtherAppWithPermissionTest() throws MyDriveException {
		try {
			myDrive.addApplication("/home/someone", "application.apk", someone, "java.lang.NullPointerException");
			myDrive.getFile("/home/someone/application.apk").setPermissions("r-x-r-x-");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}

		readFileService = new ReadFileService("/home/someone/application.apk");
		readFileService.execute();
		assertEquals("java.lang.NullPointerException", readFileService.results());
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOtherAppWithoutPermissionTest() throws MyDriveException {
		try {
			myDrive.addApplication("/home/someone", "application.apk", someone, "java.lang.NullPointerException");
			myDrive.getFile("/home/someone/application.apk").setPermissions("--x-----");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}

		readFileService = new ReadFileService("/home/someone/application.apk");
		readFileService.execute();
		// no asserts because PermissionDeniedException is expected
	}

}
