package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.TestSetupException;

@RunWith(JMockit.class)
public class EnvironmentLinksTest extends AbstractServiceTest{
	private long token;

	@Mocked
	public Link linkMock;
	
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
		MyDrive md= MyDriveService.getMyDrive();
		md.addPlainFile("/home/root/","specialLink", md.getRootUser(),content);
		Directory dir = MyDrive.getInstance().getDirectory("/home/root/sandbox");
		
		Directory dirLink = md.getDirectory("/home/root");
		for(File file : dirLink.getFilesSet()){
			if(file.getName().equals("specialLink")){
				linkMock = (Link) file;
			}
		}
		 
		
		new Expectations() {
			{
				linkMock.getFile(md.getRootUser());
				result = dir;
			}
		};
		
		ChangeDirectoryService service = new ChangeDirectoryService(token, "specialLink");
		service.execute();
		assertEquals("/home/root/sandbox",service.result());
	}
	
	@Test
	public void absoluteLinkWithVarBeg() throws MyDriveException{
		testVar("$User/sandbox");
	}
	

	protected void failVar(String path) throws MyDriveException{
		MyDrive md= MyDriveService.getMyDrive();
		md.addPlainFile("/home/root/","specialLink", md.getRootUser(),path);
		Directory dirLink = md.getDirectory("/home/root");
		for(File file : dirLink.getFilesSet()){
			if(file.getName().equals("specialLink")){
				linkMock = (Link) file;
			}
		}

		new Expectations() {
			{
				linkMock.getFile(md.getRootUser());
				result = new FileNotFoundException(); 
			}
		};
		
		ChangeDirectoryService service = new ChangeDirectoryService(token, path);
		service.execute();
	}
	@Test(expected=FileNotFoundException.class)
	public void nonExistentPath() throws MyDriveException{
		failVar("/home/$NAOEXISTE/sandbox");
	}
	
	@Test(expected=FileNotFoundException.class)
	public void nonExistentVar() throws MyDriveException{
		failVar("/home/$NAOEXISTE/sandbox");
	}
	
	@Test(expected=FileNotFoundException.class)
	public void usingOnlyDollarSign(){
		System.out.println("\u001B[31mTodo Test\u001B[0m");
	}


	
}
