package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.ContentNotLinkException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

public class CreateFileTest extends PermissionTest {
	private long token = 0;
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
		/*FIXME*/
		fail();
		MyDrive md = MyDrive.getInstance();
		
		token = getValidSession("test1", "/home/test1", new StrictTestObject());
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"PlainFile", "PlainFileTest1");
		service.execute();
		
		assertEquals("PlainFileTest1",
				md.getFileContents("/home/test1/test"));
	}
	
	@Test
	public void createFileOthersDirWithPermissionTest() {
		/*FIXME*/
		fail();
		MyDrive md = MyDrive.getInstance();
		
		token = getValidSession("test1", "/home/test3", new StrictTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"PlainFile", "PlainFileTest");
		service.execute();
		
		assertEquals("PlainFileTest",
				md.getFileContents("/home/test3/test"));
	}
	
	@Test(expected = PermissionDeniedException.class)
	public void createFileOwnDirWithoutPermissionTest() {
		/*FIXME*/
		fail();
		MyDrive md = MyDrive.getInstance();
		
		token = getValidSession("test3", "/home/test3", new StrictTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"PlainFile", "PlainFileTest1");
		service.execute();

	}
	
	@Test(expected = PermissionDeniedException.class)
	public void createFileOthersDirWithoutPermissionTest() {
		/*FIXME*/
		fail();
		MyDrive md = MyDrive.getInstance();
		
		token = getValidSession("test4", "/home/test3",new StrictTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"PlainFile", "PlainFileTest1");
		service.execute();

	}
	@Test
	public void rootCreateFileTest(){
		/*FIXME*/
		fail();
		MyDrive md = MyDrive.getInstance();
		
		token = getValidSession("root", "/home/test1", new StrictTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "test", 
				"PlainFile", "PlainFileTest");
		service.execute();
		
		assertEquals("teste", md.getFileContents("/home/test1/test"));
	}
	
	@Test(expected=InvalidFileNameException.class)
	public void CreateFileDashCharacterTest(){
		/*FIXME*/
		fail();
		MyDrive md = MyDrive.getInstance();
		
		token = getValidSession("test1", "/home/test1", new StrictTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "plain/file", 
				"PlainFile", "PlainFileTest1");
		service.execute();

	}
	
	
	@Test(expected=InvalidFileNameException.class)
	public void CreateFileNullCharacterTest(){
		/*FIXME*/
		fail();
		MyDrive md = MyDrive.getInstance();
		
		token = getValidSession("test1", "/home/test1", new StrictTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "plain\0file", 
				"PlainFile", "PlainFileTest1");
		service.execute();

	}
	
	@Test
	public void createFilePath1024CharsTest() {
		/* Currentdir = /b * 1021
		 * FIXME*/
		fail();
		MyDrive md = MyDrive.getInstance();
		
		String pathb = "/" + new String(new char[1021]).replace('\0', 'b');
		//pathb = / + b*1021 = 1022 chars
		token = getValidSession("test1", pathb, new StrictTestObject());
		

		pathb += "/b";
		//pathb = / + b*1021 + /b = 1024 chars

		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "b", 
				"PlainFile", "PlainFileTest");
		service.execute();
		
		assertEquals("PlainFileTest",
				md.getFileContents(pathb));
	}
	
	@Test(expected=InvalidFileNameException.class)
	public void createFilePath1025CharsTest() {
		/*FIXME*/
		fail();
		MyDrive md = MyDrive.getInstance();
		
		String pathc = "/" + new String(new char[1022]).replace('\0', 'c');

		token = getValidSession("test1", pathc, new StrictTestObject());
		pathc +="/c";
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "c", 
				"PlainFile", "PlainFileTest2");
		service.execute();
	}
	
	
	@Test
	public void createGoodLink(){
		/*FIXME*/
		
		fail();
		MyDrive md = MyDrive.getInstance();
		
		token = getValidSession("test1", "/home/test1", new StrictTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "testLink",
				"link",	"/home/test4/plainfile4");
		service.execute();
		
		assertEquals("/home/test1/plainfile4",
				md.getFileContents("/home/test1/testLink"));
	}
	
	@Test(expected = ContentNotLinkException.class)
	public void createBadLink(){
		/*FIXME*/
		fail();
		MyDrive md = MyDrive.getInstance();
		
		token = getValidSession("test1", "/home/test1", new StrictTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "testLink",
				"link", "olaolaola");
		service.execute();
				
	}
	
	@Test
	public void createAppAnyContent(){
		/*FIXME*/
		fail();
		MyDrive md = MyDrive.getInstance();
		
		token = getValidSession("test1", "/home/test1", new StrictTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, "testApp",
				"App", "olaolaola");
		service.execute();
		
		assertEquals("olaolaola",
				md.getFileContents("/home/test1/testApp"));
				
	}
	
	@Test
	public void createAppWithoutContent(){
		/*FIXME*/
		fail();
		MyDrive md = MyDrive.getInstance();
		
		token = getValidSession("test1", "/home/test1", new StrictTestObject());
		
		/*createFileService(token, name, type)*/
		CreateFileService service = new CreateFileService(token, "testApp",
				"App", "");
		service.execute();
		
		assertEquals("",
				md.getFileContents("/home/test1/testApp"));
				
	}
	@Test
	public void createPlainFileWithContent(){
		/*FIXME*/
		fail();
		MyDrive md = MyDrive.getInstance();
		
		token = getValidSession("test1", "/home/test1", new StrictTestObject());
		
		/*createFileService(token, name, type, content)*/
		CreateFileService service = new CreateFileService(token, 
				"PlainFileTest", "PlainFile", "olaolaola");
		service.execute();
		
		assertEquals("olaolaola",
				md.getFileContents("/home/test1/PlainFileTest"));
				
	}
	@Test
	public void createPlainFileWithoutContent(){
		/*FIXME*/
		fail();
		MyDrive md = MyDrive.getInstance();
		
		token = getValidSession("test1", "/home/test1", new StrictTestObject());
		
		/*createFileService(token, name, type)*/
		CreateFileService service = new CreateFileService(token,
				"PlainFileTest", "PlainFile", "");
		service.execute();
		
		assertEquals("",
				md.getFileContents("/home/test1/PlainFileTest"));
				
	}
	
	

}
