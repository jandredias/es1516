package pt.tecnico.myDrive.service;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Root;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

public class DeleteFileTest extends AbstractServiceTest {

	protected void populate() {
		MyDrive md = MyDrive.getInstance();
		Root root = md.getRootUser();
		try {
			md.addUser("Teste1", "Teste1", "Teste", "rwxd----");
			md.addUser("Teste2", "Teste2", "Teste2", "rwxd----");
			User teste = md.getUserByUsername("Teste");
			User teste2 = md.getUserByUsername("Teste2");

			md.addDirectory("/ola/ola", "Casa", root);
			md.addDirectory("/ola/ola", "familia",
					teste /* , delete permissions */);
			md.addPlainFile("/ola/ola/familia", "mae", teste,
					"mae"/* , delete permissions */);
			md.addPlainFile("/ola/ola/familia", "irma", teste,
					"irma"/* , without delete permissions */);

			md.addDirectory("/ola/ola", "familia2",
					teste /* , without delete permissions */);
			md.addPlainFile("/ola/ola/familia2", "mae", teste,
					"mae"/* , delete permissions */);
			md.addPlainFile("/ola/ola/familia2", "irma", teste,
					"irma"/* , delete permissions */);

			// Create token 9999 current dir /ola/ola/familia, user teste
			// Create token 1111 current dir /ola/ola/familia, user teste2
			// Create token 2222 current dir /ola/ola/familia, user root
			// Create token 3333 current dir /ola/ola user teste
			// Create token 4444 current dir /ola/ola, user teste2
			// Create token 5555 current dir /ola/ola, user root
			// Create token 6666 current dir /, user root

		} catch (Exception e) {
			log.debug("This should never occur. Clean use.");
		}
	}

	// @Test(expected = InvalidTokenException.class)
	// public void NullToken(){
	// DeleteFileService service = new DeleteFileService(null, "ola");
	// service.execute();
	// }
	//
	@Test(expected = InvalidFileNameException.class)
	public void NullName() throws MyDriveException {
		DeleteFileService service = new DeleteFileService(14561, null);
		service.execute();
	}

	//
	// @Test(expected = InvalidTokenException.class)
	// public void TokenZero(){
	// DeleteFileService service = new DeleteFileService(0, "ola");
	// service.execute();
	// }
	//
	// @Test
	// public void ValidToken(){
	// DeleteFileService service = new DeleteFileService(9999, "ola");
	// service.execute();
	// }
	//
	// @Test(expected = InvalidTokenException.class)
	// public void InvalidToken(){
	// DeleteFileService service = new DeleteFileService(8888, "ola");
	// service.execute();
	// }
	//
	// /*Delete /ola/ola/familia/pai*/
	@Test(expected = FileNotFoundException.class)
	public void FileNotExists() throws MyDriveException {
		DeleteFileService service = new DeleteFileService(9999, "pai");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void DeleteFileOwner() throws MyDriveException {
		DeleteFileService service = new DeleteFileService(9999, "mae");
		service.execute();

		MyDrive md = MyDrive.getInstance();
		md.getFile("/ola/ola/familia/mae");
	}

	@Test(expected = PermissionDeniedException.class)
	public void DeleteFileOwnerNoPermissions() throws MyDriveException {
		DeleteFileService service = new DeleteFileService(9999, "irma");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void DeleteFile() throws MyDriveException {
		DeleteFileService service = new DeleteFileService(1111, "mae");
		service.execute();

		MyDrive md = MyDrive.getInstance();
		md.getFile("/ola/ola/familia/mae");
	}

	@Test(expected = PermissionDeniedException.class)
	public void DeleteFileNoPermissions() throws MyDriveException {
		DeleteFileService service = new DeleteFileService(1111, "irma");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void DeleteFileRoot() throws MyDriveException {
		DeleteFileService service = new DeleteFileService(2222, "irma");
		service.execute();

		MyDrive md = MyDrive.getInstance();
		md.getFile("/ola/ola/familia/irma");
	}

	@Test(expected = FileNotFoundException.class)
	public void DeleteDirectoryOwnerWithPermissions() throws MyDriveException {
		DeleteFileService service = new DeleteFileService(3333, "familia2");
		service.execute();

		MyDrive md = MyDrive.getInstance();
		md.getDirectory("/ola/ola/familia2");
	}

	@Test(expected = PermissionDeniedException.class)
	public void DeleteDirectoryOwnerWithoutPermissions() throws MyDriveException {
		DeleteFileService service = new DeleteFileService(3333, "familia");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void DeleteDirectoryWithPermissions() throws MyDriveException {
		DeleteFileService service = new DeleteFileService(4444, "familia2");
		service.execute();

		MyDrive md = MyDrive.getInstance();
		md.getDirectory("/ola/ola/familia2");
	}

	@Test(expected = PermissionDeniedException.class)
	public void DeleteDirectoryWithoutPermissions() throws MyDriveException {
		DeleteFileService service = new DeleteFileService(4444, "familia");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void DeleteDirectoryRoot() throws MyDriveException {
		DeleteFileService service = new DeleteFileService(5555, "familia");
		service.execute();
		
		MyDrive md = MyDrive.getInstance();
		md.getDirectory("/ola/ola/familia");
	}

	@Test(expected = PermissionDeniedException.class)
	public void DeleteRootDirectory() throws MyDriveException {
		DeleteFileService service = new DeleteFileService(6666, "/");
		service.execute();
	}
}