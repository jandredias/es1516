package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.ContentNotAppException;
import pt.tecnico.myDrive.exception.ContentNotLinkException;
import pt.tecnico.myDrive.exception.FileIsDirectoryException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.TestSetupException;

public class CreateFileTest extends PermissionsTest {
	
	private long token = 0;
	
	private CreateFileService createFileService;
	
	@Override
	protected void populate() {
		MyDrive md = MyDrive.getInstance();
		try{
			md.addUser("test1","ola123", "test", null);
			
			md.addUser("test2", "ola123", "test", null);
			
			md.addUser("test3", "ola123", "test", "badperms");
			
			md.addUser("test4","ola123", "test", "badperms");
			
			User user1 = md.getUserByUsername("test1");
			
			String dirb = new String(new char[1010]).replace('\0', 'b');
			String dirc = new String(new char[1011]).replace('\0', 'c');

			md.addDirectory("/home/test1", dirb, user1);
			md.addDirectory("/home/test1", dirc, user1);
		}
		catch(Exception e){
			throw new TestSetupException("CreateFileService: Populate");
 
		};
	}
	
	@Override
	protected MyDriveService createService(long token, String nameOfFileItOPerates) {
		return new CreateFileService(token, "ola", "plainfile", "PlainFileTest1");
	}
	
	@Override
	protected char getPermissionChar() {
		return 'w';
	}
	
	
	@Override
	protected void assertServiceExecutedWithSuccess(){
		/*FIXME createFileService = (CreateFileService) abstractClassService; //From Upper class
		assertNotNull(createFileService.result());*/
	}
	
	private void appContent(String content) throws Exception{
		MyDrive md = MyDrive.getInstance();
		
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		CreateFileService service = new CreateFileService(token, "olaApp" , "app", content);
		
		service.execute();
		
		assertEquals(content, md.getFileContents("/home/test1/olaApp"));
	}
	
	
	/* ---------TESTS------------- */
	 
	@Test
	public void createFileOwnDirWithPermissionTest() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"plainfile", "PlainFileTest1");
		service.execute();
		
		assertEquals("PlainFileTest1",
				md.getFileContents("/home/test1/test"));
	}
	
	@Test
	public void createFileOthersDirWithPermissionTest() throws Exception {
		MyDrive md = MyDrive.getInstance();
		
		token = md.getValidToken("test1", "/home/test3", new StrictlyTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"plainfile", "PlainFileTest");
		service.execute();
		
		assertEquals("PlainFileTest",
				md.getFileContents("/home/test3/test"));
	}
	
	@Test(expected = PermissionDeniedException.class)
	public void createFileOwnDirWithoutPermissionTest() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		
		token = md.getValidToken("test3", "/home/test3", new StrictlyTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"plainfile", "PlainFileTest1");
		service.execute();

	}
	
	@Test(expected = PermissionDeniedException.class)
	public void createFileOthersDirWithoutPermissionTest() throws Exception  {
		MyDrive md = MyDrive.getInstance();		
		token = md.getValidToken("test4", "/home/test3",new StrictlyTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"plainfile", "PlainFileTest1");
		service.execute();

	}
	@Test
	public void rootCreateFileTest() throws Exception  {
		
		MyDrive md = MyDrive.getInstance();
		
		token = md.getValidToken("root", "/home/test1", new StrictlyTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"plainfile", "PlainFileTest");
		service.execute();
		
		assertEquals("PlainFileTest", md.getFileContents("/home/test1/test"));
	}
	
	@Test(expected=InvalidFileNameException.class)
	public void CreateFileDashCharacterTest() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "plain/file", 
				"plainfile", "PlainFileTest1");
		service.execute();

	}
	
	
	@Test(expected=InvalidFileNameException.class)
	public void CreateFileNullCharacterTest() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "plain\0file", 
				"plainfile", "PlainFileTest1");
		service.execute();

	}
	
	@Test
	public void createFilePath1024CharsTest() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		
		String pathb = "/home/test1/"+new String(new char[1010]).replace('\0', 'b');
		//pathb = /home/test1/ + b*1010 = 1022 chars
		token = md.getValidToken("test1", pathb, new StrictlyTestObject());
		

		pathb += "/b";
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "b", 
				"plainfile", "PlainFileTest");
		service.execute();
		
		assertEquals("PlainFileTest",
				md.getFileContents(pathb));
	}
	
	@Test(expected=InvalidFileNameException.class)
	public void createFilePath1025CharsTest() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		
		String pathc = "/home/test1/"+new String(new char[1011]).replace('\0', 'c');

		token = md.getValidToken("test1", pathc, new StrictlyTestObject());
		pathc +="/c";
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "c", 
				"plainfile", "PlainFileTest2");
		service.execute();
	}
	
	
	@Test
	public void createGoodLink() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "testLink",
				"link",	"/home/test4/plainfile4");
		service.execute();
		
		assertEquals("/home/test1/plainfile4",
				md.getFileContents("/home/test1/testLink"));
	}
	
	@Test(expected = ContentNotLinkException.class)
	public void createBadLink() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "testLink",
				"link", "ola\0ola");
		service.execute();
				
	}
	
	
	@Test(expected=ContentNotAppException.class)
	public void createAppBadContent1() throws Exception  {
		this.appContent("teste teste");
	}
	
	@Test(expected=ContentNotAppException.class)
	public void createAppBadContent2() throws Exception  {
		this.appContent("9pins");
	}
	
	@Test(expected=ContentNotAppException.class)
	public void createAppBadContent3() throws Exception  {
		this.appContent("a+c");
	}
	
	@Test(expected=ContentNotAppException.class)
	public void createAppBadContent4() throws Exception  {
		this.appContent("testing1-2-3");
	}
	
	@Test(expected=ContentNotAppException.class)
	public void createAppBadContent5() throws Exception  {
		this.appContent("O'Reily");
	}
	
	@Test(expected=ContentNotAppException.class)
	public void createAppBadContent6() throws Exception  {
		this.appContent("OReily_&_Associates");
	}
	
	@Test
	public void createAppWithoutContent() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		
		/*createFileService(token, name, type)*/
		CreateFileService service = new CreateFileService(token, "testApp",
				"app", "");
		service.execute();
		
		assertEquals("",
				md.getFileContents("/home/test1/testApp"));
				
	}
	
	@Test
	public void createPlainFileWithContent() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, 
				"PlainFileTest", "plainfile", "olaolaola");
		service.execute();
		
		assertEquals("olaolaola",
				md.getFileContents("/home/test1/PlainFileTest"));
				
	}
	@Test
	public void createPlainFileWithoutContent() throws Exception  {
		/*FIXME*/

		MyDrive md = MyDrive.getInstance();
		
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		
		/*createFileService(token, name, type)*/
		CreateFileService service = new CreateFileService(token,
				"PlainFileTest", "plainfile", "");
		service.execute();
		
		assertEquals("",
				md.getFileContents("/home/test1/PlainFileTest"));
				
	}
	
	@Test
	public void createDirectoryNoContent() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "testDir",
				"dir", "");
		service.execute();
		
		assertNotNull((Directory)md.getFile("/home/test1/testDir"));
				
	}
	
	@Test(expected=FileIsDirectoryException.class)
	public void createDirectoryAnyContent() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "testDir",
				"dir", "ola");
		service.execute();
				
	}
	
	

}
