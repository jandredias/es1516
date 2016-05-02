package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidAppContentException;
import pt.tecnico.myDrive.exception.NotPlainFileException;
import pt.tecnico.myDrive.exception.TestSetupException;


public class WriteFileTest extends PermissionsTest {
	private long token = 0;
	@Override
	protected void populate() {
		MyDrive md = MyDrive.getInstance();
		try{
			md.addUser("test1","ola123456", "test", null);
			User testUser1 = md.getUserByUsername("test1");
			
			md.addUser("test2", "ola123456", "test", "rwxdrwxd");
			User testUser2 = md.getUserByUsername("test2");
			
			md.addUser("test3", "ola123456", "test", "rwxdrwxd");
			User testUser3 = md.getUserByUsername("test3");
			testUser3.setPermissions("--------");
			//md.addUser("test4","ola123456", "test", "--------");
			//User testUser4 = md.getUserByUsername("test4");
			
			md.addPlainFile("/home/test1" , "plainfile1", testUser1, "olaola");
			md.addPlainFile("/home/test2" , "plainfile2", testUser2, "olaola");
			md.addPlainFile("/home/test3", "plainfile3", testUser3, "olaola");
			//md.addPlainFile("/home/test4", "plainfile4", testUser4, "olaola");
			
			md.addLink("/home/test1", "link1", testUser1, "/home/test1/plainfile1");
			md.addLink("/home/test1", "link2", testUser1, "/home/test1/link1");
			md.addLink("/home/test1", "link3", testUser1, "/home/test1/app1");
			md.addLink("/home/test1", "link4", testUser1, "/home/test1");
			md.addLink("/home/test1", "link5", testUser1, "brokenLink");
			
			md.addApplication("/home/test1", "app1", testUser1, "testApp" );
			
		}
		catch(Exception e){
			throw new TestSetupException("WriteFileTest: " + e.getClass() + " " + e.getMessage());
		};
		
	}

	String pwd;
	
	@Override
	protected MyDriveService createService(long token, String nameOfFileItOPerates) {
		Session s = MyDriveService.getMyDrive().getSessionByToken(token); 
		if (s != null){
			pwd = s.getCurrentDirectory().getPath();
			pwd+="/" + nameOfFileItOPerates;
		}
		return new WriteFileService(token, nameOfFileItOPerates, "ola");
	}
	
	@Override
	protected char getPermissionChar() {
		return 'w';
	}
	
	@Override
	protected void assertServiceExecutedWithSuccess(){
		PlainFile plain;
		try {
			plain = (PlainFile) MyDriveService.getMyDrive().getFile(pwd);
		} catch (FileNotFoundException e) {
			throw new TestSetupException(pwd + " does not exsts");
		}
		assertEquals("ola",plain.getContent());
	}
	
	private void appContent(String content) throws Exception{
		MyDrive md = MyDrive.getInstance();
		
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		CreateFileService service = new CreateFileService(token, "olaApp" , "app", content);
		
		service.execute();
		
		assertEquals(content, md.getFileContents("/home/test1/olaApp"));
	}
	
	/* ---------TESTS------------- */
	
	
//	@Test
//	public void writeOwnFileWithPermissionTest() throws Exception  {	
//		MyDrive md = MyDrive.getInstance();
//		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
//		WriteFileService service = new WriteFileService(token,
//				"plainfile1", "teste");
//		service.execute();
//		
//		assertEquals("teste", md.getFileContents("/home/test1/plainfile1"));
//	
//	}
//	
//	@Test
//	public void writeOthersFileWithPermissionTest() throws Exception  {
//		MyDrive md = MyDrive.getInstance();
//		token = md.getValidToken("test1", "/home/test2", new StrictlyTestObject());
//		WriteFileService service = new WriteFileService(token,
//				"plainfile2", "teste");
//		service.execute();
//		
//		assertEquals("teste", md.getFileContents("/home/test2/plainfile2"));
//	}
//	
//	@Test(expected = PermissionDeniedException.class)
//	public void writeOwnFileWithoutPermissionTest() throws Exception  {
//		
//		MyDrive md = MyDrive.getInstance();
//		token = md.getValidToken("test3", "/home/test3", new StrictlyTestObject());
//		WriteFileService service = new WriteFileService(token, "plainfile3", "teste");
//		service.execute();
//
//	}
//	
//	@Test(expected = PermissionDeniedException.class)
//	public void writeOthersFileWithoutPermissionTest() throws Exception  {
//		
//		MyDrive md = MyDrive.getInstance();
//		token = md.getValidToken("test4", "/home/test3", new StrictlyTestObject());
//		WriteFileService service = new WriteFileService(token, "plainfile3", "teste");
//		service.execute();
//
//	}
	
	@Test
	public void rootWriteFileTest() throws Exception  {
		
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("root", "/home/test3", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token, "plainfile3", "teste");
		service.execute();
		
		assertEquals("teste", md.getFileContents("/home/test3/plainfile3"));
	}
	
	@Test(expected=NotPlainFileException.class)
	public void writeDir() throws Exception  {
		
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("test1", "/home", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token, "test1",
				"ola123");
		service.execute();
		
	}
	
	@Test(expected=FileNotFoundException.class)
	public void writeToBrokenLink() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token, "link5",
				"doesntmatter");
		service.execute();
	}
	
	@Test(expected=NotPlainFileException.class)
	public void writeToDirectoryLink() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token, "link4",
				"doesntmatter");
		service.execute();
	}
	
	@Test
	public void writeToPlainFileLink() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token, "link1",
				"doesntmatter");
		service.execute();
		
		assertEquals("doesntmatter",
				md.getFileContents("/home/test1/plainfile1"));
	}
	
	@Test
	public void writeToAppFileLink() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token, "link3",
				"doesntmatter");
		service.execute();
		
		assertEquals("doesntmatter",
				md.getFileContents("/home/test1/app1"));
	}
	
	@Test
	public void writeToLinkFileLink() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token, "link2",
				"doesntmatter");
		service.execute();
		
		assertEquals("doesntmatter",
				md.getFileContents("/home/test1/plainfile1"));
	}
	
	public void writeApp() throws Exception{
		MyDrive md = MyDrive.getInstance();
		WriteFileService service = new WriteFileService(token, "app1",
				"test.writing.app");
		service.execute();
		
		assertEquals("",
				md.getFileContents("/home/test1/app1"));
	}
	
	@Test(expected=InvalidAppContentException.class)
	public void writeAppBadContent1() throws Exception  {
		this.appContent("teste teste");
	}
	
	@Test(expected=InvalidAppContentException.class)
	public void writeAppBadContent2() throws Exception  {
		this.appContent("9pins");
	}
	
	@Test(expected=InvalidAppContentException.class)
	public void writeAppBadContent3() throws Exception  {
		this.appContent("a+c");
	}
	
	@Test(expected=InvalidAppContentException.class)
	public void writeAppBadContent4() throws Exception  {
		this.appContent("testing1-2-3");
	}
	
	@Test(expected=InvalidAppContentException.class)
	public void writeAppBadContent5() throws Exception  {
		this.appContent("O'Reily");
	}
	
	@Test(expected=InvalidAppContentException.class)
	public void writeAppBadContent6() throws Exception  {
		this.appContent("OReily_&_Associates");
	}
	
	
	
	
}
