package pt.tecnico.myDrive.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exception.*;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;


public class WriteFileTest extends PermissionsTest {
	private long token = 0; //FIXME
	@Override
	protected void populate() {
		MyDrive md = MyDrive.getInstance();
		try{
			md.addUser("test1","ola123", "test", null);
			User testUser1 = md.getUserByUsername("test1");
			
			md.addUser("test2", "ola123", "test", null);
			User testUser2 = md.getUserByUsername("test2");
			
			md.addUser("test3", "ola123", "test", "badperms");
			User testUser3 = md.getUserByUsername("test3");
			
			md.addUser("test4","ola123", "test", "badperms");
			User testUser4 = md.getUserByUsername("test4");
			
			md.addPlainFile("/home/test1" , "plainfile1", testUser1, "olaola");
			md.addPlainFile("/home/test2" , "plainfile2", testUser2, "olaola");
			md.addPlainFile("/home/test3", "plainfile3", testUser3, "olaola");
			md.addPlainFile("/home/test4", "plainfile4", testUser4, "olaola");
			
			md.addLink("/home/test1", "link1", testUser1,
					"/home/test1/plainfile1");
			//FIXME md.addApplication("/home/test1", "app1", testUser1, );
			
		}
		catch(Exception e){};
		
	}
	
	/*FIXME  Perguntem ao amaral que eu tmb nao sei*/
	
	@Override
	protected MyDriveService createTokenService(long token) {
		return new WriteFileService(token, "ola", "PlainFileTest1");
	}

	@Override
	protected MyDriveService createPermissionsService(long token, String nameOfFileItOPerates) {
		return new WriteFileService(token, "ola", "PlainFileTest1");
	}
	
	@Override
	protected char getPermissionChar() {
		return 'w';
	}
	
	
	@Override
	protected void assertServiceExecutedWithSuccess(){
		/*FIXME createFileService = (CreateFileService) permissionsService; //From Upper class
		assertNotNull(createFileService.result());*/
	}
	
	/* ---------TESTS------------- */
	
	
	@Test
	public void writeOwnFileWithPermissionTest() throws Exception  {
		/*FIXME user1 needs to be logged in and currentdir contain plainfile1*/
		
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token,
				"plainfile1", "teste");
		service.execute();
		
		assertEquals("teste", md.getFileContents("/home/test1/plainfile1"));
	
	}
	
	@Test
	public void writeOthersFileWithPermissionTest() throws Exception  {
		/*FIXME*/
		
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("test1", "/home/test3", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token,
				"plainfile3", "teste");
		service.execute();
		
		assertEquals("teste", md.getFileContents("/home/test3/plainfile3"));
	}
	
	@Test(expected = PermissionDeniedException.class)
	public void writeOwnFileWithoutPermissionTest() throws Exception  {
		
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("test3", "/home/test3", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token, "plainfile3", "teste");
		service.execute();

	}
	
	@Test(expected = PermissionDeniedException.class)
	public void writeOthersFileWithoutPermissionTest() throws Exception  {
		
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("test4", "/home/test3", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token, "plainfile3", "teste");
		service.execute();

	}
	@Test
	public void rootWriteFileTest() throws Exception  {
		
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("root", "/home/test1", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token, "plainfile1", "teste");
		service.execute();
		
		assertEquals("teste", md.getFileContents("/home/test1/plainfile1"));
	}
	
	@Test
	public void writeGoodLink() throws Exception  {
		
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token, "link1",
				"/home/test4/plainfile4");
		service.execute();
		
		assertEquals("/home/test1/plainfile4",
				md.getFileContents("/home/test1/link1"));
	}
	
	@Test(expected = ContentNotLinkException.class)
	public void writeBadLink() throws Exception  {
		
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("test1", "/home/test1", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token, "link1",
				"olaolaola");
		service.execute();
				
	}
	/* FIXME don't know what APP's content must be
	 * 
	 *
	@Test
	public void writeGoodApp(){
		/*FIXME user with permissions must be logged in and currentdir contain
		 * app1
		
		MyDrive md = MyDrive.getInstance();
		WriteFileService service = new WriteFileService(token, "app1",
				"");
		service.execute();
		
		assertEquals("",
				md.getFileContents("/home/test1/app1"));
	}
	
	@Test(expected = ContentNotAppException.class)
	public void writeBadApp(){
		/*FIXME user with permissions must be logged in and currentdir contain
		 * link1
		
		MyDrive md = MyDrive.getInstance();
		WriteFileService service = new WriteFileService(token, "app1",
				"olaolaola")
		service.execute();
				
	}
	*/
	@Test(expected = UnsupportedOperationException.class)
	public void writeContentOfDir() throws Exception  {
		MyDrive md = MyDrive.getInstance();
		token = md.getValidToken("test1", "/home", new StrictlyTestObject());
		WriteFileService service = new WriteFileService(token, "test1",
				"olaolaola");
		service.execute();
				
	}
	
	
	
	
}
