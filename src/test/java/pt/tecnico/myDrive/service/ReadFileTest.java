package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class ReadFileTest extends AbstractServiceTest {

	private MyDrive myDrive;
	private User me, someone;
	private ReadFileService readFileService;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		myDrive = MyDrive.getInstance();

		myDrive.addUser("me", "qwerty123", "Jimmy", null);
		myDrive.addUser("someone", "qwerty123", "Sarah", null);

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
		readFileService.dispatch();
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
		readFileService.dispatch();
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
		readFileService.dispatch();
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
		readFileService.dispatch();
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
		readFileService.dispatch();
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
		readFileService.dispatch();
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
		readFileService.dispatch();
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
		readFileService.dispatch();
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
		readFileService.dispatch();
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
		readFileService.dispatch();
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
		readFileService.dispatch();
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
		readFileService.dispatch();
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
		readFileService.dispatch();
		// no asserts because PermissionDeniedException is expected
	}

	@Override
	protected void populate() {
		// TODO Auto-generated method stub
	}

	@After
	public void tearDown() {
		super.tearDown();
	}

}
