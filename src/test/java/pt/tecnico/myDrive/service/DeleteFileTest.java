package pt.tecnico.myDrive.service;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Root;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.TestSetupException;

public class DeleteFileTest extends AbstractServiceTest {

	private User teste1;
	private User teste2;
	
	protected void populate() {
		MyDrive md = MyDrive.getInstance();
		Root root = md.getRootUser();
		try {
			md.addUser("teste1", "teste1", "teste1", "rwxd----");
			md.addUser("teste2", "teste2", "teste2", "rwxd----");
			teste1 = md.getUserByUsername("teste1");
			teste2 = md.getUserByUsername("teste2");


			//md.addDirectory("/home/teste1", "Casa", teste);
			md.getDirectory("/home").setPermissions("-w-d-w-d");
			md.getDirectory("/home/teste1").setPermissions("-w-d-w-d");
			
			md.addDirectory("/home/teste1", "familia",teste1 /* , delete permissions */);
			md.getDirectory("/home/teste1/familia").setPermissions("-w-d-w-d");
			
//			//All delete permissions
//			md.addPlainFile("/home/teste1/familia", "mae", teste1, "mae"/* , delete permissions */);
//			md.getFile("/home/teste1/familia/mae").setPermissions("-w-d-w-d");
			
//			//None delete permissions
//			md.addPlainFile("/home/teste1/familia", "irma", teste1,	"irma"/* , without delete permissions */);
//			md.getFile("/home/teste1/familia/irma").setPermissions("-w---w--");
			
//			//No owner permissions
//			md.addPlainFile("/home/teste1/familia", "irmao", teste1,	"irmao"/* , without delete permissions */);
//			md.getFile("/home/teste1/familia/irmao").setPermissions("-w---w-d");
			
//			//Owner permissions
//			md.addPlainFile("/home/teste1/familia", "primo", teste1,	"primo"/* , without delete permissions */);
//			md.getFile("/home/teste1/familia/primo").setPermissions("-w-d-w--");

//			//Folder with write permissions and file with owner permissions
//			md.addDirectory("/home/teste1", "writePownerP",	teste1 /* , without delete permissions */);
//			md.getDirectory("/home/teste1/writePownerP").setPermissions("-w-d-w--");
//			md.addPlainFile("/home/teste1/writePownerP", "ownerP", teste1,	"ownerP"/* , without delete permissions */);
//			md.getFile("/home/teste1/writePownerP/ownerP").setPermissions("-w-d-w--");
//			
//			//Folder with write permissions and file with no owner permissions
//			md.addDirectory("/home/teste1", "writePNoownerP",	teste1 /* , without delete permissions */);
//			md.getDirectory("/home/teste1/writePNoownerP").setPermissions("-w-d-w--");
//			md.addPlainFile("/home/teste1/writePNoownerP", "NoownerP", teste1,	"NoownerP"/* , without delete permissions */);
//			md.getFile("/home/teste1/writePNoownerP/NoownerP").setPermissions("-w---w-d");

//			md.addPlainFile("/home/teste1/familia2", "mae", teste,
//					"mae"/* , delete permissions */);
//			md.addPlainFile("/home/teste1/familia2", "irma", teste,
//					"irma"/* , delete permissions */);

			// Create token 9999 current dir /ola/ola/familia, user teste
			// Create token 1111 current dir /ola/ola/familia, user teste2
			// Create token 2222 current dir /ola/ola/familia, user root
			// Create token 3333 current dir /ola/ola user teste
			// Create token 4444 current dir /ola/ola, user teste2
			// Create token 5555 current dir /ola/ola, user root
			// Create token 6666 current dir /, user root

		} catch (Exception e) {
			log.debug("This should never occur. Clean use.");
			throw new TestSetupException("DeleteFileTest: Populate");
		}
	}

	// @Test(expected = InvalidTokenException.class)
	// public void NullToken(){
	// DeleteFileService service = new DeleteFileService(null, "ola");
	// service.execute();
	// }
//	//
//	@Test(expected = InvalidFileNameException.class)
//	public void NullName() throws MyDriveException {
//		DeleteFileService service = new DeleteFileService(14561, null);
//		service.execute();
//	}
//
//	// @Test(expected = InvalidTokenException.class)
//	// public void NullToken(){
//	// DeleteFileService service = new DeleteFileService(null, "ola");
//	// service.execute();
//	// }
//	//
//	/*
//	 * @Test(expected = InvalidFileNameException.class) public void NullName()
//	 * throws MyDriveException { DeleteFileService service = new
//	 * DeleteFileService(14561, null); service.execute(); }
//	 */
//
//	//
//	// @Test(expected = InvalidTokenException.class)
//	// public void TokenZero(){
//	// DeleteFileService service = new DeleteFileService(0, "ola");
//	// service.execute();
//	// }
//	//
//	// @Test
//	// public void ValidToken(){
//	// DeleteFileService service = new DeleteFileService(9999, "ola");
//	// service.execute();
//	// }
//	//
//	// @Test(expected = InvalidTokenException.class)
//	// public void InvalidToken(){
//	// DeleteFileService service = new DeleteFileService(8888, "ola");
//	// service.execute();
//	// }
//	//
	 /*Delete /home/teste1/familia/pai*/
	@Test(expected = FileNotFoundException.class)
	public void FileNotExists() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t9999 = md.getValidToken("teste1", "/home/teste1/familia", new StrictlyTestObject());

		DeleteFileService service = new DeleteFileService(t9999, "file");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void DeleteFileOwner() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t9999 = md.getValidToken("teste1", "/home/teste1/familia", new StrictlyTestObject());
		//Owner permissions
		md.addPlainFile("/home/teste1/familia", "file", teste1,	"file"/* , without delete permissions */);
		md.getFile("/home/teste1/familia/file").setPermissions("-w-d-w--");

		DeleteFileService service = new DeleteFileService(t9999, "file");
		service.execute();

		md.getFile("/home/teste1/familia/file");
	}

	@Test(expected = PermissionDeniedException.class)
	public void DeleteFileOwnerNoPermissions() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t9999 = md.getValidToken("teste1", "/home/teste1/familia", new StrictlyTestObject());
		//None delete permissions
		md.addPlainFile("/home/teste1/familia", "irma", teste1,	"irma"/* , without delete permissions */);
		md.getFile("/home/teste1/familia/irma").setPermissions("-w---w--");

		DeleteFileService service = new DeleteFileService(t9999, "irma");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void DeleteFile() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste2", "/home/teste1/familia", new StrictlyTestObject());
		//No owner permissions
		md.addPlainFile("/home/teste1/familia", "file", teste1,	"file"/* , without delete permissions */);
		md.getFile("/home/teste1/familia/file").setPermissions("-w---w-d");

		DeleteFileService service = new DeleteFileService(t1111, "file");
		service.execute();

		md.getFile("/home/teste1/familia/file");
	}

	@Test(expected = PermissionDeniedException.class)
	public void DeleteFileNoPermissions() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste2", "/home/teste1/familia", new StrictlyTestObject());
		//None delete permissions
		md.addPlainFile("/home/teste1/familia", "file", teste1,	"file"/* , without delete permissions */);
		md.getFile("/home/teste1/familia/file").setPermissions("-w---w--");

		DeleteFileService service = new DeleteFileService(t1111, "file");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void DeleteFileRoot() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t2222 = md.getValidToken("root", "/home/teste1/familia", new StrictlyTestObject());
		//None delete permissions
		md.addPlainFile("/home/teste1/familia", "file", teste1,	"file"/* , without delete permissions */);
		md.getFile("/home/teste1/familia/file").setPermissions("-w---w--");

		DeleteFileService service = new DeleteFileService(t2222, "file");
		service.execute();

		md.getFile("/home/teste1/familia/file");
	}

	@Test(expected = FileNotFoundException.class)
	public void DeleteDirectoryOwnerWithPermissions() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t3333 = md.getValidToken("teste1", "/home/teste1", new StrictlyTestObject());
		
		//Folder with owner delete permissions
		md.addDirectory("/home/teste1", "file",	teste1 );
		md.getDirectory("/home/teste1/file").setPermissions("---d----");

		DeleteFileService service = new DeleteFileService(t3333, "file");
		service.execute();

		md.getDirectory("/home/teste1/file");
	}

	@Test(expected = PermissionDeniedException.class)
	public void DeleteDirectoryOwnerWithoutPermissions() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t3333 = md.getValidToken("teste1", "/home/teste1/", new StrictlyTestObject());
		//Folder with no owner delete permissions
		md.addDirectory("/home/teste1", "file",	teste1);
		md.getDirectory("/home/teste1/file").setPermissions("-------d");

		DeleteFileService service = new DeleteFileService(t3333, "file");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void DeleteDirectoryWithPermissions() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t4444 = md.getValidToken("teste2", "/home/teste1", new StrictlyTestObject());
		//Folder with delete permissions
		md.addDirectory("/home/teste1", "file",	teste1);
		md.getDirectory("/home/teste1/file").setPermissions("-------d");

		DeleteFileService service = new DeleteFileService(t4444, "file");
		service.execute();

		md.getDirectory("/home/teste1/writePownerP");
	}

	@Test(expected = PermissionDeniedException.class)
	public void DeleteDirectoryWithoutPermissions() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t4444 = md.getValidToken("teste2", "/home/teste1", new StrictlyTestObject());
		//Folder without delete permissions
				md.addDirectory("/home/teste1", "file",	teste1 /* , without delete permissions */);
				md.getDirectory("/home/teste1/file").setPermissions("---d----");

		
		DeleteFileService service = new DeleteFileService(t4444, "file");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void DeleteDirectoryUserRoot() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t5555 = md.getValidToken("root", "/home/teste1", new StrictlyTestObject());
		//Folder without delete permissions and root user
		md.addDirectory("/home/teste1", "file",	teste1 /* , without delete permissions */);
		md.getDirectory("/home/teste1/file").setPermissions("-------");

		DeleteFileService service = new DeleteFileService(t5555, "file");
		service.execute();

		md.getDirectory("/home/teste1/file");
	}
	
	@Test(expected = FileNotFoundException.class)
	public void DeleteDirectoryOwnerPermissionsWithExecute() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		
		//Folder with execute permissions and file with owner permissions
		md.addDirectory("/home/teste1", "dir",	teste1 /* , without delete permissions */);
		md.getDirectory("/home/teste1/dir").setPermissions("-w---w--");
		
		long t5555 = md.getValidToken("teste1", "/home/teste1/dir", new StrictlyTestObject());
		
		md.addPlainFile("/home/teste1/dir", "file", teste1,	"ownerP"/* , without delete permissions */);
		md.getFile("/home/teste1/dir/file").setPermissions("---d----");

		DeleteFileService service = new DeleteFileService(t5555, "file");
		service.execute();

		md.getDirectory("/home/teste1/dir/file");
	}
	
	@Test(expected = PermissionDeniedException.class)
	public void DeleteDirectoryWithoutOwnerPermissionsWithExecute() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		
		//Folder with execute permissions and file with owner permissions
		md.addDirectory("/home/teste1", "dir",	teste1 /* , without delete permissions */);
		md.getDirectory("/home/teste1/dir").setPermissions("-w---w--");
		
		long t5555 = md.getValidToken("teste1", "/home/teste1/dir", new StrictlyTestObject());
		
		md.addPlainFile("/home/teste1/dir", "file", teste1,	"ownerP"/* , without delete permissions */);
		md.getFile("/home/teste1/dir/file").setPermissions("-------d");

		DeleteFileService service = new DeleteFileService(t5555, "file");
		service.execute();

		
	}
	
	@Test(expected = FileNotFoundException.class)
	public void DeleteDirectorywithPermissionsWithExecute() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		
		//Folder with execute permissions and file with owner permissions
		md.addDirectory("/home/teste1", "dir",	teste1 /* , without delete permissions */);
		md.getDirectory("/home/teste1/dir").setPermissions("-w---w--");
		
		long t5555 = md.getValidToken("teste2", "/home/teste1/dir", new StrictlyTestObject());
		
		md.addPlainFile("/home/teste1/dir", "file", teste1,	"ownerP"/* , without delete permissions */);
		md.getFile("/home/teste1/dir/file").setPermissions("-------d");

		DeleteFileService service = new DeleteFileService(t5555, "file");
		service.execute();

		md.getDirectory("/home/teste1/dir/file");
	}
	
	@Test(expected = PermissionDeniedException.class)
	public void DeleteDirectoryWithoutPermissionsWithExecute() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		
		//Folder with execute permissions and file with owner permissions
		md.addDirectory("/home/teste1", "dir",	teste1 /* , without delete permissions */);
		md.getDirectory("/home/teste1/dir").setPermissions("-w---w--");
		
		long t5555 = md.getValidToken("teste2", "/home/teste1/dir", new StrictlyTestObject());
		
		md.addPlainFile("/home/teste1/dir", "file", teste1,	"ownerP"/* , without delete permissions */);
		md.getFile("/home/teste1/dir/file").setPermissions("---d----");

		DeleteFileService service = new DeleteFileService(t5555, "file");
		service.execute();

		
	}
	
	@Test(expected = PermissionDeniedException.class)
	public void DeleteDirectoryWithPermissionsWithoutOwnerExecute() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		
		//Folder with execute permissions and file with owner permissions
		md.addDirectory("/home/teste1", "dir",	teste1 /* , without delete permissions */);
		md.addPlainFile("/home/teste1/dir", "file", teste1,	"ownerP"/* , without delete permissions */);
		
		
		long t5555 = md.getValidToken("teste1", "/home/teste1/dir", new StrictlyTestObject());
		
		md.getDirectory("/home/teste1/dir").setPermissions("-----w--");
		md.getFile("/home/teste1/dir/file").setPermissions("---d---d");

		DeleteFileService service = new DeleteFileService(t5555, "file");
		service.execute();

		
	}
	
	@Test(expected = PermissionDeniedException.class)
	public void DeleteDirectoryWithPermissionsWithoutExecute() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		
		//Folder with execute permissions and file with owner permissions
		md.addDirectory("/home/teste1", "dir",	teste1 /* , without delete permissions */);
		md.addPlainFile("/home/teste1/dir", "file", teste1,	"ownerP"/* , without delete permissions */);
		
		
		long t5555 = md.getValidToken("teste2", "/home/teste1/dir", new StrictlyTestObject());
		
		md.getDirectory("/home/teste1/dir").setPermissions("-w------");
		md.getFile("/home/teste1/dir/file").setPermissions("---d---d");

		DeleteFileService service = new DeleteFileService(t5555, "file");
		service.execute();

	}
	

	@Test(expected = PermissionDeniedException.class)
	public void DeleteRootDirectory() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t6666 = md.getValidToken("root", "/", new StrictlyTestObject());

		DeleteFileService service = new DeleteFileService(t6666, "/");
		service.execute();
	}
}
