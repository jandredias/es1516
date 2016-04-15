package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.TestSetupException;

public class ChangeDirectoryTest extends PermissionsTest{

	private MyDrive myDrive;
	Long rootToken;
	Long teste1Token;
	Long teste2Token;

	protected void populate(){
		myDrive = MyDriveService.getMyDrive();
		try{
			myDrive.addUser("teste1", "teste1", "teste1", "rwxd----");
			myDrive.addUser("teste2", "teste2", "teste2", "rwxd----");

			myDrive.addDirectory("/home/teste1", "everyoneReadsThis", myDrive.getUserByUsername("teste1"));
			myDrive.addDirectory("/home/teste2", "abc", myDrive.getUserByUsername("teste2"));
			myDrive.addDirectory("/home/teste2", "notAllowed", myDrive.getUserByUsername("teste2"));
			myDrive.addDirectory("/home/teste2/abc", "def", myDrive.getUserByUsername("teste2"));

			myDrive.getDirectory("/home/teste2/notAllowed").setPermissions("--------");
			myDrive.getDirectory("/home/teste1/everyoneReadsThis").setPermissions("--------");
			myDrive.addPlainFile("/home/teste1", "file", myDrive.getUserByUsername("teste1"), "File's content");

			teste1Token = myDrive.getValidToken("teste1", "/home/teste1", new StrictlyTestObject());
			teste2Token = myDrive.getValidToken("teste2", "/home/teste2", new StrictlyTestObject());
			rootToken = myDrive.getValidToken("root", "/", new StrictlyTestObject());
		}catch(MyDriveException e){
			throw new TestSetupException("ChangeDirectory: Populate");
		}
	}

//	//I'm not sure what this is supposed to do
//	protected MyDriveService createTokenService(long token){
//		return null;
//	}


	@Test(expected = FileNotFoundException.class)
	public void noPathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, null);
		service.execute();
	}

	@Test(expected = FileNotFoundException.class) 
	public void emptyPathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "");
		service.execute();
	}

	@Test
	public void validAbsolutePathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "/home");
		service.execute();
		assertEquals("/home", service.result());
	}

	@Test
	public void selfAbsolutePathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken,"/./home");
		service.execute();
		assertEquals("/home", service.result());
	}

	@Test
	public void parentAbsolutePathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken,"/home/../home");
		service.execute();
		assertEquals("/home", service.result());
	}

	@Test
	public void selfParentAbsolutePathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken,"/./home/../home/root");
		service.execute();
		assertEquals("/home/root", service.result());
	}

	@Test
	public void rootParentAbsolutePathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken,"/../home");
		service.execute();
		assertEquals("/home", service.result());
	}

	@Test(expected = FileNotFoundException.class) //FIXME
	public void invalidSyntaxAbsolutePathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken,"//home//");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void absolutePathDoesNotExistTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken,"/this/path/does/not/exists");
		service.execute();
	}

	@Test(expected = PermissionDeniedException.class)
	public void noPermissionsAbsolutePathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(teste1Token,"/home/teste2");
		service.execute();
	}

	@Test(expected = PermissionDeniedException.class)
	public void noPermissionsTargetDirectoryAbsolutePathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(teste1Token, "/home/teste1/everyoneReadsThis");
		service.execute();
		assertEquals("/home/teste1/everyoneReadsThis", service.result());
	}

	@Test(expected = FileNotFoundException.class)
	public void AbsolutePathIsNotDirectoryTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "/home/teste1/file");
		service.execute();
	}

	@Test
	public void relativePathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(teste2Token, "abc");
		service.execute();
		assertEquals("/home/teste2/abc", service.result());
	}

	@Test
	public void selfOnlyRelativePathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(teste2Token, ".");
		service.execute();
		assertEquals("/home/teste2", service.result());
	}

	@Test
	public void parentOnlyRelativePathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(teste2Token, "..");
		service.execute();
		assertEquals("/home", service.result());
	}

	@Test
	public void selfRelativePathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(teste2Token, "abc/./def");
		service.execute();
		assertEquals("/home/teste2/abc/def", service.result());
	}

	@Test
	public void parentRelativePathTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(teste2Token, "abc/../abc");
		service.execute();
		assertEquals("/home/teste2/abc", service.result());
	}
	
	@Test
	public void checkRelativePathSelfAndParentTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(teste2Token, "abc/./../abc");
		service.execute();
		assertEquals("/home/teste2/abc", service.result());
	}

	@Test
	public void relativePathParentOverRootTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "../../../../../../../home/teste2");
		service.execute();
		assertEquals("/home/teste2", service.result());
	}

	@Test(expected = FileNotFoundException.class)
	public void relativePathDoesNotExistsTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(teste2Token, "teste2/ola/ola");
		service.execute();
	}

	@Test(expected = PermissionDeniedException.class)
	public void relativePathHasNoListingPermissionsTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(teste2Token, "notAllowed");
		service.execute();
	}

	@Test
	public void relativePathTargetDirectoryListingPermissionsTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(teste2Token, "abc");
		service.execute();
		assertEquals("/home/teste2/abc", service.result());
	}

	@Test(expected = FileNotFoundException.class)
	public void relativePathIsFileTest() throws MyDriveException {
		ChangeDirectoryService service = new ChangeDirectoryService(teste1Token, "file");
		service.execute();
	}

	private String pwd;
	@Override
	protected MyDriveService createService(long token, String ignore) {
		
		MyDrive md = MyDriveService.getMyDrive();
		Session session = md.getSessionByToken(token);
		if(session != null){
			pwd = session.getCurrentDirectory().getPath();
			pwd += "/changeDir";
		}
		return new ChangeDirectoryService(token,"changeDir");
	}

	@Override
	protected void assertServiceExecutedWithSuccess() {
		ChangeDirectoryService changeDirectoryService = (ChangeDirectoryService) abstractClassService; //From Upper class
		assertEquals(pwd, changeDirectoryService.result());
	}

	@Override
	protected char getPermissionChar() {
		return 'x';
	}

}