package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
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
			myDrive.getFile("/home/me/myLink").setPermissions("r-------");
		} catch (MyDriveException e) {
			fail("Should not have thrown exception");
		}
		
		assertEquals("/home/me/myFile.txt", readFileService.readFile("/home/me/myLink"));
	}

	@Test
	public void readOwnLinkWithoutPermissionTest() {
		fail("Not yet implemented");
	}

	@Test
	public void readOtherLinkWithPermissionTest() {
		fail("Not yet implemented");
	}

	@Test
	public void readOtherLinkWithoutPermissionTest() {
		fail("Not yet implemented");
	}

	@Test
	public void readOwnAppWithPermissionTest() {
		fail("Not yet implemented");
	}

	@Test
	public void readOwnAppWithoutPermissionTest() {
		fail("Not yet implemented");
	}

	@Test
	public void readOtherAppWithPermissionTest() {
		fail("Not yet implemented");
	}

	@Test
	public void readOtherAppWithoutPermissionTest() {
		fail("Not yet implemented");
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
