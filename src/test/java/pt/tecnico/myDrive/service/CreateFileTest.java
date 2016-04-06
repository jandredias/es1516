package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.ContentNotLinkException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

public class CreateFileTest extends AbstractServiceTest {

	@Override
	protected void populate() {
		MyDrive md = MyDrive.getInstance();
		try{
			md.addUser("test1","ola123", "test", null);
			
			md.addUser("test2", "ola123", "test", null);
			
			md.addUser("test3", "ola123", "test", "badperms");
			
			md.addUser("test4","ola123", "test", "badperms");
			
			User user1 = md.getUserByUsername("test1");
			
			String pathb = "/" + new String(new char[1021]).replace('\0', 'b');
			String pathc = "/" + new String(new char[1022]).replace('\0', 'c');

			md.addDirectory(pathb, "b", user1);
			md.addDirectory(pathc, "c", user1);
		}
		catch(Exception e){};
	}
	
	
	@Test
	public void createFileOwnDirWithPermissionTest() {
		/*FIXME user1 needs to be logged in and currentdir contain plainfile1*/
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"PlainFile", "PlainFileTest1");
		service.execute();
		
		assertEquals("PlainFileTest1",
				md.getFileContents("/home/test1/test"));
	}
	
	@Test
	public void createFileOthersDirWithPermissionTest() {
		/*FIXME user1 or 2 needs to be logged in and currentdir contain 
		 * plainfile3*/
		fail("FIX ME");
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"PlainFile", "PlainFileTest");
		service.execute();
		
		assertEquals("PlainFileTest",
				md.getFileContents("/home/test3/test"));
	}
	
	@Test(expected = PermissionDeniedException.class)
	public void createFileOwnDirWithoutPermissionTest() {
		/*FIXME user3 needs to be logged in and currentdir contain plainfile3*/
		fail("FIX ME");
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"PlainFile", "PlainFileTest1");
		service.execute();

	}
	
	@Test(expected = PermissionDeniedException.class)
	public void createFileOthersDirWithoutPermissionTest() {
		/*FIXME user4 needs to be logged in and currentdir contain plainfile3*/
		fail("FIX ME");
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"PlainFile", "PlainFileTest1");
		service.execute();

	}
	@Test
	public void rootCreateFileTest(){
		/*FIXME root needs to be logged and currentdir contain plainfile1*/
		fail("FIX ME");
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"PlainFile", "PlainFileTest");
		service.execute();
		
		assertEquals("teste", md.getFileContents("/home/test1/test"));
	}
	
	@Test(expected=InvalidFileNameException.class)
	public void CreateFileDashCharacterTest(){
		/*FIXME root needs to be logged and currentdir contain plainfile1*/
		fail("FIX ME");
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "plain/file", 
				"PlainFile", "PlainFileTest1");
		service.execute();

	}
	
	
	@Test(expected=InvalidFileNameException.class)
	public void CreateFileNullCharacterTest(){
		/*FIXME root needs to be logged and currentdir contain plainfile1*/
		fail("FIX ME");
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "plain\0file", 
				"PlainFile", "PlainFileTest1");
		service.execute();

	}
	
	@Test
	public void createFilePath1024CharsTest() {
		/* Currentdir = /b * 1021
		 * FIXME user1 needs to be logged in and currentdir contain plainfile1*/
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "b", 
				"PlainFile", "PlainFileTest");
		service.execute();
		
		String pathb = "/" + new String(new char[1021]).replace('\0', 'b') +
				"/b";
		
		assertEquals("PlainFileTest",
				md.getFileContents(pathb));
	}
	
	@Test(expected=InvalidFileNameException.class)
	public void createFilePath1025CharsTest() {
		/* Currentdir = /c * 1022
		 * FIXME user1 needs to be logged*/
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "c", 
				"PlainFile", "PlainFileTest2");
		service.execute();
	}
	
	
	@Test
	public void createGoodLink(){
		/*FIXME user with permissions must be logged in and currentdir contain
		 * link1*/
		fail("FIX ME");
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "testLink",
				"link",	"/home/test4/plainfile4");
		service.execute();
		
		assertEquals("/home/test1/plainfile4",
				md.getFileContents("/home/test1/testLink"));
	}
	
	@Test(expected = ContentNotLinkException.class)
	public void createBadLink(){
		/*FIXME user with permissions must be logged in and currentdir contain
		 * link1*/
		fail("FIX ME");
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "testLink",
				"link", "olaolaola");
		service.execute();
				
	}
	
	@Test
	public void createAppAnyContent(){
		/*FIXME user with permissions must be logged in and currentdir contain
		 * link1*/
		fail("FIX ME");
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "testApp",
				"App", "olaolaola");
		service.execute();
		
		assertEquals("olaolaola",
				md.getFileContents("/home/test1/testApp"));
				
	}
	
	@Test
	public void createAppWithoutContent(){
		/*FIXME user with permissions must be logged in and currentdir contain
		 * link1*/
		fail("FIX ME");
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type)*/
		CreateFileService service = new CreateFileService(token, "testApp",
				"App" ,"olaolaola");
		service.execute();
		
		assertEquals("",
				md.getFileContents("/home/test1/testApp"));
				
	}
	@Test
	public void createPlainFileWithContent(){
		/*FIXME user with permissions must be logged in and currentdir contain
		 * link1*/
		fail("FIX ME");
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, 
				"PlainFileTest", "PlainFile", "olaolaola");
		service.execute();
		
		assertEquals("olaolaola",
				md.getFileContents("/home/test1/PlainFileTest"));
				
	}
	@Test
	public void createPlainFileWithoutContent(){
		/*FIXME user with permissions must be logged in and currentdir contain
		 * link1*/
		fail("FIX ME");
		MyDrive md = MyDrive.getInstance();
		/*createFileService(token, name, type)*/
		CreateFileService service = new CreateFileService(token, 
				"PlainFileTest", "PlainFile");
		service.execute();
		
		assertEquals("",
				md.getFileContents("/home/test1/PlainFileTest"));
				
	}
	
	

}
