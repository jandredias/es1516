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

	protected void populate() {
		MyDrive md = MyDrive.getInstance();
		Root root = md.getRootUser();
		try {
			md.addUser("teste1", "teste1", "teste1", "rwxd----");
			md.addUser("teste2", "teste2", "teste2", "rwxd----");
			User teste = md.getUserByUsername("teste1");
			User teste2 = md.getUserByUsername("teste2");


			md.addDirectory("/home/teste1", "Casa", teste);
			md.getDirectory("/home").setPermissions("-w-d-w-d");
			md.getDirectory("/home/teste1").setPermissions("-w-d-w-d");
			
			md.addDirectory("/home/teste1", "familia",teste /* , delete permissions */);
			md.getDirectory("/home/teste1/familia").setPermissions("-w-d-w-d");
			
			//Owner with delete any with delete
			md.addPlainFile("/home/teste1/familia", "mae", teste, "mae"/* , delete permissions */);
			md.getFile("/home/teste1/familia/mae").setPermissions("-w-d-w-d");
			
			//Owner 
			md.addPlainFile("/home/teste1/familia", "irma", teste,	"irma"/* , without delete permissions */);
			md.getFile("/home/teste1/familia/irma").setPermissions("-w---w--");
			
			md.addPlainFile("/home/teste1/familia", "irmao", teste,	"irmao"/* , without delete permissions */);
			md.getFile("/home/teste1/familia/irmao").setPermissions("-w---w-d");
			
			md.addPlainFile("/home/teste1/familia", "primo", teste,	"primo"/* , without delete permissions */);
			md.getFile("/home/teste1/familia/primo").setPermissions("-w-d-w--");
//
//			md.addDirectory("/home/teste1", "familia2",
//					teste /* , without delete permissions */);
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
	// /*Delete /home/teste1/familia/pai*/
	@Test(expected = FileNotFoundException.class)
	public void FileNotExists() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t9999 = md.getValidToken("teste1", "/home/teste1/familia", new StrictlyTestObject());

		DeleteFileService service = new DeleteFileService(t9999, "pai");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void DeleteFileOwner() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t9999 = md.getValidToken("teste1", "/home/teste1/familia", new StrictlyTestObject());

		DeleteFileService service = new DeleteFileService(t9999, "primo");
		service.execute();

		md.getFile("/home/teste1/familia/mae");
	}

	@Test(expected = PermissionDeniedException.class)
	public void DeleteFileOwnerNoPermissions() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t9999 = md.getValidToken("teste1", "/home/teste1/familia", new StrictlyTestObject());

		DeleteFileService service = new DeleteFileService(t9999, "irma");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void DeleteFile() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste2", "/home/teste1/familia", new StrictlyTestObject());

		DeleteFileService service = new DeleteFileService(t1111, "irmao");
		service.execute();

		md.getFile("/home/teste1/familia/mae");
	}
//
//	@Test(expected = PermissionDeniedException.class)
//	public void DeleteFileNoPermissions() throws MyDriveException {
//		MyDrive md = MyDrive.getInstance();
//		long t1111 = md.getValidToken("teste2", "/home/teste1/familia", new StrictlyTestObject());
//
//		DeleteFileService service = new DeleteFileService(t1111, "irma");
//		service.execute();
//	}
//
//	@Test(expected = FileNotFoundException.class)
//	public void DeleteFileRoot() throws MyDriveException {
//		MyDrive md = MyDrive.getInstance();
//		long t2222 = md.getValidToken("root", "/home/teste1/familia", new StrictlyTestObject());
//
//		DeleteFileService service = new DeleteFileService(t2222, "irma");
//		service.execute();
//
//		md.getFile("/home/teste1/familia/irma");
//	}
//
//	@Test(expected = FileNotFoundException.class)
//	public void DeleteDirectoryOwnerWithPermissions() throws MyDriveException {
//		MyDrive md = MyDrive.getInstance();
//		long t3333 = md.getValidToken("teste1", "/home/teste1", new StrictlyTestObject());
//
//		DeleteFileService service = new DeleteFileService(t3333, "familia2");
//		service.execute();
//
//		md.getDirectory("/home/teste1/familia2");
//	}
//
//	@Test(expected = PermissionDeniedException.class)
//	public void DeleteDirectoryOwnerWithoutPermissions() throws MyDriveException {
//		MyDrive md = MyDrive.getInstance();
//		long t3333 = md.getValidToken("teste1", "/home/teste1", new StrictlyTestObject());
//
//		DeleteFileService service = new DeleteFileService(t3333, "familia");
//		service.execute();
//	}
//
//	@Test(expected = FileNotFoundException.class)
//	public void DeleteDirectoryWithPermissions() throws MyDriveException {
//		MyDrive md = MyDrive.getInstance();
//		long t4444 = md.getValidToken("teste2", "/home/teste1", new StrictlyTestObject());
//
//		DeleteFileService service = new DeleteFileService(t4444, "familia2");
//		service.execute();
//
//		md.getDirectory("/home/teste1/familia2");
//	}
//
//	@Test(expected = PermissionDeniedException.class)
//	public void DeleteDirectoryWithoutPermissions() throws MyDriveException {
//		MyDrive md = MyDrive.getInstance();
//		long t4444 = md.getValidToken("teste2", "/home/teste1", new StrictlyTestObject());
//
//		DeleteFileService service = new DeleteFileService(t4444, "familia");
//		service.execute();
//	}
//
//	@Test(expected = FileNotFoundException.class)
//	public void DeleteDirectoryRoot() throws MyDriveException {
//		MyDrive md = MyDrive.getInstance();
//		long t5555 = md.getValidToken("root", "/home/teste1", new StrictlyTestObject());
//
//		DeleteFileService service = new DeleteFileService(t5555, "familia");
//		service.execute();
//
//		md.getDirectory("/home/teste1/familia");
//	}
//
//	@Test(expected = PermissionDeniedException.class)
//	public void DeleteRootDirectory() throws MyDriveException {
//		MyDrive md = MyDrive.getInstance();
//		long t6666 = md.getValidToken("root", "/", new StrictlyTestObject());
//
//		DeleteFileService service = new DeleteFileService(t6666, "/");
//		service.execute();
//	}
}
