package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;

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

	@Test
	public void readOwnFileWithoutPermissionTest() {
		fail("Not yet implemented");
	}

	@Test
	public void readOtherFileWithPermissionTest() {
		fail("Not yet implemented");
	}

	@Test
	public void readOtherFileWithoutPermissionTest() {
		fail("Not yet implemented");
	}

	@Test
	public void readDirectoryTest() {
		fail("Not yet implemented");
	}

	@Test
	public void readOwnLinkWithPermissionTest() {
		fail("Not yet implemented");
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
