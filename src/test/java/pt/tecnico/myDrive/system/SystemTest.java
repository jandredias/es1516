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
		/*new Import(sh).execute(new String[] { "filename" });
		new LoginUser(sh).execute(new String[] { "user", "pass" });
		new ChangeDirectory(sh).execute(new String[] { "path" });
		new List(sh).execute(new String[] { });
		new AddFile(sh).execute(new String[] { "fileName", "fileType", "content" });
		new Write(sh).execute(new String[] { "path", "text" });
		new Execute(sh).execute(new String[] { "path" });
		new Key(sh).execute(new String[] { "user" });
		new Environment(sh).execute(new String[] { "name", "value" });*/
	}

	@After
	@Atomic
	public void tearDown() {
		MyDrive.getInstance().cleanup();
	}
}
