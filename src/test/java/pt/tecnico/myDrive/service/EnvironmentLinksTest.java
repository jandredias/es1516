package pt.tecnico.myDrive.service;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.TestSetupException;

@RunWith(JMockit.class)
public class EnvironmentLinksTest extends AbstractServiceTest{
	private long token;
	
	@Override
	protected void populate() {
		try{
			MyDrive md = MyDrive.getInstance();
			token = md.getValidToken("root", "/home/root/", new StrictlyTestObject() );
			md.addDirectory("/home/root", "sandbox", md.getRootUser());
		}
		catch(Exception e){
			throw new TestSetupException("EnvironmentLinksTest: " + e.getClass() + " " + e.getMessage());
		};
	}
	
	
	protected void testVar(String content) throws MyDriveException{
		MyDrive md = MyDriveService.getMyDrive();
		md.addPlainFile("/home/root/","specialLink", md.getRootUser(),content);
		Directory dir = MyDrive.getInstance().getDirectory("/home/root/sandbox");
		new MockUp<MyDrive>() {
			@Mock
			Directory getDirectory(String name = "/home/root/specialLink" , User user)  throws FileNotFoundException, PermissionDeniedException { 
				return dir; 
			}
		};
		
		ChangeDirectoryService service = new ChangeDirectoryService(token, path);
		service.execute();
		assertEquals("/home/root/sandbox",service.result());
	}
	
	@Test
	protected void absoluteLinkWithVarBeg() throws MyDriveException{
		testVar("$User/sandbox");
	}
	

	protected void failVar(String path) throws MyDriveException{
		new MockUp<MyDrive>() {
			@Mock
			Directory getDirectory(String path, User user)  throws FileNotFoundException, PermissionDeniedException { throw new FileNotFoundException(); }
		};
		
		ChangeDirectoryService service = new ChangeDirectoryService(token, path);
		service.execute();
	}
	@Test(expected=FileNotFoundException.class)
	public void nonExistentPath() throws MyDriveException{
		failVar("/home/$NAOEXISTE/sandbox");
	}
	
	@Test(expected=FileNotFoundException.class)
	public void nonExistentVar(){
		failVar("/home/$NAOEXISTE/sandbox");
	}
	
	@Test(expected=FileNotFoundException.class)
	public void usingOnlyDollarSign(){
		System.out.println("\u001B[31mTodo Test\u001B[0m");
	}
*/


	
}
