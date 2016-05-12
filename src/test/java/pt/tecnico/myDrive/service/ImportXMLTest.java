package pt.tecnico.myDrive.service;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import java.io.StringReader;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.WriteOnReadError;
import pt.tecnico.myDrive.MyDriveApplication;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.TestSetupException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.UsernameAlreadyInUseException;
import pt.tecnico.myDrive.exception.InvalidPasswordException;
import pt.tecnico.myDrive.exception.ImportDocumentException;

public class ImportXMLTest extends AbstractServiceTest {

    private final String xmlUsers = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ "  <user username=\"Mariazita\">"
	+ "  </user>"
	+ "  <user username=\"Manel\">"
	+ "    <password>\"ola123456\"</password>"
	+ "    <name>\"Manel\"</name>"
	+ "    <mask>\"rwxd----\"</mask>"
	+ "    <home>\"/home/Manel\"</home>"
	+ "  </user>"
	+ "</myDrive>";
    
    private final String xmlFullPlainfile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ " <plain>"
	+ "		<name>\"plainfile\"</name>"
	+ "		<permissions>\"rwxd----\"</permissions>"
	+ "		<owner>\"root\"</owner>"
	+ "		<contents>\"olaolaola\"</contents>"
	+ "		<path>\"/home/test1\"</path>"
	+ "	</plain>"
	+ "</myDrive>";
    
    private final String xmlPlainfileNoContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ " <plain>"
	+ "		<name>\"plainfile\"</name>"
	+ "		<permissions>\"rwxd----\"</permissions>"
	+ "		<owner>\"root\"</owner>"
	+ "		<path>\"/home/test1\"</path>"
	+ "	</plain>"
	+ "</myDrive>";
    
    private final String xmlMinimalPlain = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ " <plain>"
	+ "		<name>\"plainfile\"</name>"
	+ "		<path>\"/home/test1\"</path>"
	+ "	</plain>"
	+ "</myDrive>";
    
    private final String xmlFullDirectory = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ " <dir>"
	+ "		<name>\"testDir\"</name>"
	+ "		<permissions>\"rwxd----\"</permissions>"
	+ "		<owner>\"root\"</owner>"
	+ "		<path>\"/home/olaola/olaola\"</path>"
	+ "	</dir>"
	+ "</myDrive>";
    
    private final String xmlMinimalDirectory = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ " <dir>"
	+ "		<name>\"testDir\"</name>"
	+ "		<path>\"/home/test1\"</path>"
	+ "	</dir>"
	+ "</myDrive>";
    
    private final String xmlFullApp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ " <plain>"
	+ "		<name>\"app\"</name>"
	+ "		<permissions>\"rwxd----\"</permissions>"
	+ "		<owner>\"root\"</owner>"
	+ "		<contents>\"this.content.ola\"</contents>"
	+ "		<path>\"/home/test1\"</path>"
	+ "	</plain>"
	+ "</myDrive>";
    
    private final String xmlAppNoContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ " <plain>"
	+ "		<name>\"app\"</name>"
	+ "		<permissions>\"rwxd----\"</permissions>"
	+ "		<owner>\"root\"</owner>"
	+ "		<path>\"/home/test1\"</path>"
	+ "	</plain>"
	+ "</myDrive>";
    
    private final String xmlCompleteApp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ " <plain>"
	+ "		<name>\"plainfile\"</name>"
	+ "		<path>\"/home/test1\"</path>"
	+ "	</plain>"
	+ "</myDrive>";
    
    private final String xmlFullLink = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ " <plain>"
	+ "		<name>\"link\"</name>"
	+ "		<permissions>\"rwxd----\"</permissions>"
	+ "		<owner>\"root\"</owner>"
	+ "		<value>\"/home/test1\"</value>"
	+ "		<path>\"/home/test1\"</path>"
	+ "	</plain>"
	+ "</myDrive>";
    
    private final String xmlLinkNoContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ " <plain>"
	+ "		<name>\"link\"</name>"
	+ "		<permissions>\"rwxd----\"</permissions>"
	+ "		<owner>\"root\"</owner>"
	+ "		<path>\"/home/test1\"</path>"
	+ "	</plain>"
	+ "</myDrive>";
    
    private final String xmlCompleteLink = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ " <plain>"
	+ "		<name>\"link\"</name>"
	+ "		<value>\"/home/test1\"</value>"
	+ "		<path>\"/home/test1\"</path>"
	+ "	</plain>"
	+ "</myDrive>";
    
    
    
    private final String invalidPassword = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ "  <user username=\"Manel\">"
	+ "    <password>\"ola\"</password>"
	+ "    <name>\"Manel\"</name>"
	+ "    <mask>\"rwxd----\"</mask>"
	+ "    <home>\"/home/Manel\"</home>"
	+ "  </user>"
	+ "</myDrive>";
    
    
    private final String existingUser = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ "  <user username=\"test1\">"
	+ "  <password>\"olaolaolaola\"</password>"
	+ "  </user>"
	+ "</myDrive>";
    
    private final String existingFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<myDrive>"
	+ "  <plain>"
	+ "    <name>\"plainfile1\"</name>"
	+ "    <path>\"/home/test1\"</path>"
	+ "  </plain>"
	+ "</myDrive>";
    
	
	@Override
	protected void populate() {
		MyDrive md = MyDrive.getInstance();
		try{
			md.addUser("test1","ola123456", "test", null);
			User testUser1 = md.getUserByUsername("test1");
			md.addPlainFile("/home/test1" , "plainfile1", testUser1, "olaola");
		}	catch(Exception e){
			throw new TestSetupException("ImportXMLTest: " + e.getClass() + " " + e.getMessage());
		};

	}
	
	@Test
    public void createsAllUsers() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xmlUsers));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
        
        MyDrive md = MyDrive.getInstance();
        assertEquals(5,md.getUsers().size());
        assertNotNull(md.getUserByUsername("Mariazita"));
        assertNotNull(md.getUserByUsername("Manel"));
        
	}
	
	@Test
    public void completesMissingUserFields() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xmlUsers));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
        
        MyDrive md = MyDrive.getInstance();
        assertNotNull(md.getUserByUsername("Mariazita"));
        assertNotNull(md.getUserByUsername("Mariazita").getName());
        assertNotNull(md.getUserByUsername("Mariazita").getUsersHome());
        assertNotNull(md.getUserByUsername("Mariazita").getPermissions());

        
	}
	
	@Test
    public void createsPlainFile() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xmlFullPlainfile));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
        
        MyDrive md = MyDrive.getInstance();
        assertNotNull(md.getFile("/home/test1/plainfile"));
        assertEquals("plainfile", md.getFile("/home/test1/plainfile").getName());
        assertEquals("rwxd----", md.getFile("/home/test1/plainfile").getPermissions());
        assertEquals("root", md.getFile("/home/test1/plainfile").getOwner().getUsername());
        assertEquals("olaolaola", md.getFileContents("/home/test1/plainfile"));

	}
	
	@Test
    public void createsPlainFileNoContent() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xmlPlainfileNoContent));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
        
        MyDrive md = MyDrive.getInstance();
        assertNotNull(md.getFile("/home/test1/plainfile"));
        assertEquals("plainfile", md.getFile("/home/test1/plainfile").getName());
        assertEquals("rwxd----", md.getFile("/home/test1/plainfile").getPermissions());
        assertEquals("root", md.getFile("/home/test1/plainfile").getOwner().getUsername());
        assertEquals("", md.getFileContents("/home/test1/plainfile"));

	}
	
	@Test
    public void completesPlainFile() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xmlMinimalPlain));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
        
        MyDrive md = MyDrive.getInstance();
        assertNotNull(md.getFile("/home/test1/plainfile"));
        assertEquals("plainfile", md.getFile("/home/test1/plainfile").getName());
        assertEquals("rwxd----", md.getFile("/home/test1/plainfile").getPermissions());
        assertEquals("root", md.getFile("/home/test1/plainfile").getOwner().getUsername());
        assertEquals("", md.getFileContents("/home/test1/plainfile"));

	}
	
	
	@Test
    public void createsDir() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xmlFullDirectory));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
        
        MyDrive md = MyDrive.getInstance();
        assertNotNull(md.getFile("/home/olaola/olaola/testDir"));
        assertEquals("root", md.getFile("/home/olaola/olaola/testDir").getOwner().getUsername());
        assertEquals("rwxd----", md.getFile("/home/olaola/olaola/testDir").getPermissions());
        assertEquals("testDir", md.getFile("/home/olaola/olaola/testDir").getName());
        
	}
	
	
	@Test
    public void createsMissingDir() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xmlFullDirectory));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
        
        MyDrive md = MyDrive.getInstance();
        assertNotNull(md.getFile("/home/olaola/olaola"));
        
	}
	
	@Test
    public void completesDir() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xmlMinimalDirectory));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
        
        MyDrive md = MyDrive.getInstance();
        assertNotNull(md.getFile("/home/test1/testDir"));
        assertEquals("root", md.getFile("/home/test1/testDir").getOwner().getUsername());
        assertEquals("rwxd----", md.getFile("/home/test1/testDir").getPermissions());
        assertEquals("testDir", md.getFile("/home/test1/testDir").getName());
        
	}
	
	@Test
    public void createsFullApp() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xmlFullApp));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
        
        MyDrive md = MyDrive.getInstance();
        assertNotNull(md.getFile("/home/test1/app"));
        assertEquals("app", md.getFile("/home/test1/app").getName());
        assertEquals("rwxd----", md.getFile("/home/test1/app").getPermissions());
        assertEquals("root", md.getFile("/home/test1/app").getOwner().getUsername());
        assertEquals("olaolaola", md.getFileContents("/home/test1/app"));

	}
	
	@Test
    public void createsAppNoContent() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xmlAppNoContent));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
        
        MyDrive md = MyDrive.getInstance();
        assertNotNull(md.getFile("/home/test1/app"));
        assertEquals("app", md.getFile("/home/test1/app").getName());
        assertEquals("rwxd----", md.getFile("/home/test1/app").getPermissions());
        assertEquals("root", md.getFile("/home/test1/app").getOwner().getUsername());
        assertEquals("", md.getFileContents("/home/test1/app"));

	}
	
	@Test
    public void completesApp() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xmlCompleteApp));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
        
        MyDrive md = MyDrive.getInstance();
        assertNotNull(md.getFile("/home/test1/app"));
        assertEquals("app", md.getFile("/home/test1/app").getName());
        assertEquals("rwxd----", md.getFile("/home/test1/app").getPermissions());
        assertEquals("root", md.getFile("/home/test1/app").getOwner().getUsername());
        assertEquals("", md.getFileContents("/home/test1/app"));

	}
	
	@Test
    public void createsFullLink() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xmlFullLink));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
        
        MyDrive md = MyDrive.getInstance();
        assertNotNull(md.getFile("/home/test1/link"));
        assertEquals("link", md.getFile("/home/test1/link").getName());
        assertEquals("rwxd----", md.getFile("/home/test1/link").getPermissions());
        assertEquals("root", md.getFile("/home/test1/link").getOwner().getUsername());
        assertEquals("/home/test1", md.getFileContents("/home/test1/link"));

	}
	
	@Test(expected=ImportDocumentException.class)
    public void createsLinkNoContent() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xmlLinkNoContent));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();

	}
	
	@Test
    public void completeLink() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xmlCompleteLink));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
        
        MyDrive md = MyDrive.getInstance();
        assertNotNull(md.getFile("/home/test1/link"));
        assertEquals("link", md.getFile("/home/test1/link").getName());
        assertEquals("rwxd----", md.getFile("/home/test1/link").getPermissions());
        assertEquals("root", md.getFile("/home/test1/link").getOwner().getUsername());
        assertEquals("/home/test1", md.getFileContents("/home/test1/link"));

	}
	
	
	@Test(expected=ImportDocumentException.class)
    public void duplicateFile() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(this.existingFile));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
        
	}
	
	@Test(expected=ImportDocumentException.class)
    public void duplicateUser() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(this.existingUser));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
	}
	
	@Test(expected=ImportDocumentException.class)
    public void invalidPassword() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(this.invalidPassword));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();
	}
	
	

}
