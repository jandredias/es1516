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

		readFileService = new ReadFileService();
	}

	@Test
	public void readOwnFileWithPermissionTest() {
		try {
			myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
			myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}

		assertEquals("qwerty", readFileService.readFile("/home/me/myFile.txt"));
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOwnFileWithoutPermissionTest() {
		try {
			myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
			myDrive.getFile("/home/me/myFile.txt").setPermissions("--------");
		} catch (MyDriveException e) {
			fail("Should have not thrown exception");
		}

		readFileService.readFile("/home/me/myFile.txt");
		// no asserts because PermissionDeniedException is expected
	}

	@Test
	public void readOtherFileWithPermissionTest() {
		try {
			myDrive.addPlainFile("/home/someone", "theirFile.txt", someone, "qwerty");
			myDrive.getFile("/home/someone/theirFile.txt").setPermissions("----r---");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}

		assertEquals("qwerty", readFileService.readFile("/home/someone/theirFile.txt"));
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOtherFileWithoutPermissionTest() {
		try {
			myDrive.addPlainFile("/home/someone", "theirFile.txt", someone, "qwerty");
			myDrive.getFile("/home/someone/theirFile.txt").setPermissions("--------");
		} catch (MyDriveException e) {
			fail("Should have not thrown exception");
		}

		readFileService.readFile("/home/someone/theirFile.txt");
		// no asserts because PermissionDeniedException is expected
	}

	@Test(expected = UnsupportedOperationException.class)
	public void readDirectoryTest() {
		try {
			myDrive.addDirectory("/home/me", "durr", me);
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}
		
		readFileService.readFile("/home/me/durr");
		// no asserts because UnsupportedOperationException is expected
	}

	@Test
	public void readOwnLinkWithPermissionTest() {
		try {
			myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
			myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");
			myDrive.addLink("/home/me", "myLink", me, "/home/me/myFile.txt");
			myDrive.getFile("/home/me/myLink").setPermissions("r-------"); // TODO check link permissions
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}
		
		assertEquals("/home/me/myFile.txt", readFileService.readFile("/home/me/myLink"));
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOwnLinkWithoutPermissionTest() {
		try {
			myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
			myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");
			myDrive.addLink("/home/me", "myLink", me, "/home/me/myFile.txt");
			myDrive.getFile("/home/me/myLink").setPermissions("--------"); // TODO check link permissions
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}
		
		readFileService.readFile("/home/me/myLink");
		// no asserts because PermissionDeniedException is expected
	}

	@Test
	public void readOtherLinkWithPermissionTest() {
		try {
			myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
			myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");
			myDrive.addLink("/home/someone", "myLink", someone, "/home/me/myFile.txt");
			myDrive.getFile("/home/someone/myLink").setPermissions("----r---"); // TODO check link permissions
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}
		
		assertEquals("/home/me/myFile.txt", readFileService.readFile("/home/someone/myLink"));
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOtherLinkWithoutPermissionTest() {
		try {
			myDrive.addPlainFile("/home/me", "myFile.txt", me, "qwerty");
			myDrive.getFile("/home/me/myFile.txt").setPermissions("r-------");
			myDrive.addLink("/home/someone", "myLink", someone, "/home/me/myFile.txt");
			myDrive.getFile("/home/someone/myLink").setPermissions("--------"); // TODO check link permissions
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}
		
		readFileService.readFile("/home/someone/myLink");
		// no asserts because PermissionDeniedException is expected
	}

	@Test
	public void readOwnAppWithPermissionTest() {
		try {
			myDrive.addApplication("/home/me", "application.apk", me, "java.lang.NullPointerException");
			myDrive.getFile("/home/me/application.apk").setPermissions("r-x-----");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}
		
		assertEquals("java.lang.NullPointerException", readFileService.readFile("/home/me/application.apk"));
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOwnAppWithoutPermissionTest() {
		try {
			myDrive.addApplication("/home/me", "application.apk", me, "java.lang.NullPointerException");
			myDrive.getFile("/home/me/application.apk").setPermissions("--x-----");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}
		
		readFileService.readFile("/home/me/application.apk");
		// no asserts because PermissionDeniedException is expected
	}

	@Test
	public void readOtherAppWithPermissionTest() {
		try {
			myDrive.addApplication("/home/someone", "application.apk", someone, "java.lang.NullPointerException");
			myDrive.getFile("/home/someone/application.apk").setPermissions("r-x-r-x-");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}
		
		assertEquals("java.lang.NullPointerException", readFileService.readFile("/home/someone/application.apk"));
	}

	@Test(expected = PermissionDeniedException.class)
	public void readOtherAppWithoutPermissionTest() {
		try {
			myDrive.addApplication("/home/someone", "application.apk", someone, "java.lang.NullPointerException");
			myDrive.getFile("/home/someone/application.apk").setPermissions("--x-----");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}
		
		readFileService.readFile("/home/someone/application.apk");
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
