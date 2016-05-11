package pt.tecnico.myDrive.system;

import org.junit.After;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.tecnico.myDrive.domain.MyDrive;
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

public class SystemTest extends AbstractServiceTest {

	private MyDriveShell sh;

	protected void populate() {
		sh = new MyDriveShell();
	}

	@Test
	public void success() {
		/*new Import(sh).execute(new String[] { "drive.xml" });
		new LoginUser(sh).execute(new String[] { "teste", "123456789" });
		new ChangeDirectory(sh).execute(new String[] { "/home/teste" });
		new List(sh).execute(new String[] { });
		new AddFile(sh).execute(new String[] { "testFile", "app" });
		new Write(sh).execute(new String[] { "/home/teste/testFile", "text" });
		new Execute(sh).execute(new String[] { "/home/teste/testFile" });
		new Key(sh).execute(new String[] { "root" });
		new Environment(sh).execute(new String[] { "testVar", "testValue" });*/
	}

	@After
	@Atomic
	public void tearDown() {
		MyDrive.getInstance().cleanup();
	}
}
