package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.PermissionDeniedException;


//public class ListDirectoryTest extends TokenServiceTest {

public class ListDirectoryTest extends AbstractServiceTest {

	protected void populate() {
		
		
		MyDrive md = MyDrive.getInstance();
/*
		Person p = new Person(pb, "João");
		new Person(pb, "António");

		new Contact(p, "António", 123456);
*/
	}
/*
	private Contact getContact(String personName, String contactName) {
		Person p = PhoneBookService.getPhoneBook().getPersonByName(personName);
		return p.getContactByName(contactName);
	}
*/
	/* ---------------------------------------------------------------------- */
	/* ------------------------ Permissions Related ------------------------- */
	/* ---------------------------------------------------------------------- */

	@Test
	public void ownUserHasPermissions(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}

	@Test
	public void otherUserHasPermissions(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}
	
	@Test(expected = PermissionDeniedException.class)
	public void ownUserHasNoPermissions(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}
		
	@Test(expected = PermissionDeniedException.class)
	public void otherUserHasNoPermissions(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}

	@Test
	public void rootUserHasNoPermissions(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}
	
	/* ---------------------------------------------------------------------- */
	/* --------------------------List Generally ----------------------------- */
	/* ---------------------------------------------------------------------- */
	@Test
	public void listEmptyDirectory(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}
	
	@Test
	public void listDirectoryWithPlainFile(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}
	
	@Test
	public void listDirectoryWithDirectory(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}
	
	@Test
	public void listDirectoryWithlink(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}
	
	@Test
	public void listDirectoryWithApp(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}
		
	@Test
	public void listDirectoryWith6Files(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}
	
	/* ---------------------------------------------------------------------- */
	/* -------------------------- Dimension Tests --------------------------- */
	/* ---------------------------------------------------------------------- */
	
	@Test
	public void emptyPlainFileDimension(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}
	

	@Test
	public void char_1_PlainFileDimension(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}
	
	@Test
	public void char_1000_PlainFileDimension(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}
	
	@Test
	public void emptyDirectoryDimension(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}
	
	@Test
	public void files_3_DirectoryDimension(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}
	
	@Test
	public void linkDimension(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}

	@Test
	public void appDimension(){
		assert false;//FIXME:TODO:XXX
		long token = 0;// = getValidToken();
		ListDirectoryService service = new ListDirectoryService(token);
	}

}
