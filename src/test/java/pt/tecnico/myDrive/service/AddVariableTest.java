package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidValueException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.TestSetupException;
import pt.tecnico.myDrive.exception.VarNotFoundException;

public class AddVariableTest extends TokenAccessTest {

	long token;
	String tokenFile;

	protected void populate() {
		MyDrive md = MyDrive.getInstance();

		try {
			md.addUser("teste1", "teste1234", "teste1", "rwxd----");
			long t1111 = md.getValidToken("teste1", "/home/teste1/familia", new StrictlyTestObject());
			t1111.addVariable("var2", "varvalue2");

		} catch (Exception e) {
			log.debug("This should never occur. Clean use.");
			throw new TestSetupException("AddVariableTest: Populate");
		}
	}

	@Override
	protected MyDriveService createService(long token, String nameOfFileItOPerates) {
		this.token = token;
		return new AddVariableService(token, "ola", "ola1");
	}

	@Override
	protected void assertServiceExecutedWithSuccess() {

		Session session = MyDriveService.getMyDrive().getSessionByToken(this.token);

		assertNotNull(session.getVariablesSet());

	}

	@Test
	public void NewVariable() {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste2", "/home/teste1/familia", new StrictlyTestObject());

		AddVariableService service = new AddVariableService(t1111, "var1", "varvalue1");
		service.execute();

		assertEquals("varvalue1", md.getVarValue("var1"));
	}

	@Test
	public void ExistingVariable() {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste2", "/home/teste1/familia", new StrictlyTestObject());

		AddVariableService service = new AddVariableService(t1111, "var2", "varvalue3");
		service.execute();

		assertEquals("varvalue3", md.getVarValue("var1"));
	}

	@Test(expected = VarNotFoundException.class)
	public void NullName() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste2", "/home/teste1/familia", new StrictlyTestObject());

		AddVariableService service = new AddVariableService(t1111, null, "varvalue1");
		service.execute();

	}

	@Test(expected = InvalidValueException.class)
	public void NullValue() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste2", "/home/teste1/familia", new StrictlyTestObject());

		AddVariableService service = new AddVariableService(t1111, "var1", null);
		service.execute();

	}
}
