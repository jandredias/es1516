package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mockit.Mock;
import mockit.MockUp;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.TestSetupException;

/*
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

	}
	
	@Test
	public void absoluteLinkWithVarBeg() throws MyDriveException{
		String content = "$Home/User/"; 
		
		MyDrive md= MyDriveService.getMyDrive();
		md.addLink("/home/root/","specialLink", md.getRootUser(),content);
		Directory dir = MyDrive.getInstance().getDirectory("/home/root/");


		new MockUp<Link>() {
			  @Mock
			  File getFile(User user){ return dir; }
		};		 
		
		
		ChangeDirectoryService service = new ChangeDirectoryService(token, "specialLink");
		service.execute();
		assertEquals("/home/root",service.result());
	}
	

	public Link linkMock = null;
	protected void failVar(String path) throws MyDriveException{
		
		MyDrive md= MyDriveService.getMyDrive();
		md.addLink("/home/root/","specialLink", md.getRootUser(),path);

		new MockUp<Link>() {
			  @Mock
			  File getFile(User user) throws FileNotFoundException{ throw new FileNotFoundException(); }
		};		 


		ChangeDirectoryService service = new ChangeDirectoryService(token, path);
		service.execute();
	}
	
	@Test(expected=FileNotFoundException.class)
	public void nonExistentPath() throws MyDriveException{
		failVar("/home/$NAOEXISTE/");
	}
	
	@Test(expected=FileNotFoundException.class)
	public void nonExistentVar() throws MyDriveException{
		failVar("/home/$NAOEXISTE/");
	}
	
	@Test(expected=FileNotFoundException.class)
	public void usingOnlyDollarSign() throws MyDriveException{
		failVar("/home/$/");
	}


	
}
*/