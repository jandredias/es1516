package pt.tecnico.myDrive.system;

import org.junit.After;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Root;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.Variable;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.presentation.AddFile;
import pt.tecnico.myDrive.presentation.ChangeDirectory;
import pt.tecnico.myDrive.presentation.Environment;
import pt.tecnico.myDrive.presentation.Execute;
import pt.tecnico.myDrive.presentation.Import;
import pt.tecnico.myDrive.presentation.Key;
import pt.tecnico.myDrive.presentation.List;
import pt.tecnico.myDrive.presentation.LoginUser;
import pt.tecnico.myDrive.presentation.MyDriveShell;
import pt.tecnico.myDrive.presentation.Write;
import pt.tecnico.myDrive.service.AbstractServiceTest;
import pt.tecnico.myDrive.service.ChangeDirectoryService;
import pt.tecnico.myDrive.service.LoginUserService;

public class SystemTest extends AbstractServiceTest {

	private MyDriveShell sh;

	protected void populate() {
		sh = new MyDriveShell();
	}

	@Test
	public void success() {
		new Import(sh).execute(new String[] { "drive.xml" });
		new LoginUser(sh).execute(new String[] { "teste", "123456789" });
		new ChangeDirectory(sh).execute(new String[] { "/home/teste" });
		new List(sh).execute(new String[] {});
		new AddFile(sh).execute(new String[] { "testFile", "app" });
		new Write(sh).execute(new String[] { "/home/teste/testFile", "text" });
		new Execute(sh).execute(new String[] { "/home/teste/testFile" });
		new Key(sh).execute(new String[] {});
		new Environment(sh).execute(new String[] { "testVar", "testValue" });
	}

	@After
	@Atomic
	public void tearDown() {

		MyDrive mD = MyDrive.getInstance();

		long token = sh.getTokenByUsername("teste");

		try {

			new LoginUserService("nobody", "").execute();

			new ChangeDirectoryService(sh.getCurrentToken(), "/home/nobody").execute();

			Session session = mD.getSessionByToken(token);

			for (Variable var : session.getVariablesSet()) {
				session.removeVariables(var);
			}

			mD.removeSession(mD.getSessionByToken(token));

			Root root = mD.getRootUser();

			Directory rootDir = mD.getRootDirectory();

			File file = rootDir.getFile("/home/teste/testFile", root);
			file.delete(root);

			file = rootDir.getFile("/home/teste", root);
			file.delete(root);

			User user = mD.getUserByUsername("teste");

			mD.removeUsers(user);

		} catch (MyDriveException e) {
			// Won't happen... I hope so...
		}
	}
}
