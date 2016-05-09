package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

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

	private User teste1;
	String tokenFile;

	protected void populate() {
		MyDrive md = MyDrive.getInstance();

		try {
			long t1111 = md.getValidToken("teste2", "/home/teste1/familia", new StrictlyTestObject());
			md.addVariable(t1111, "var2", "varvalue2");

		} catch (Exception e) {
			log.debug("This should never occur. Clean use.");
			throw new TestSetupException("DeleteFileTest: Populate");
		}
	}

	@Override
	protected MyDriveService createService(long token, String nameOfFileItOPerates) {
		MyDrive md = MyDriveService.getMyDrive();
		Session session = md.getSessionByToken(token);
		if (session != null)
			tokenFile = session.getCurrentDirectory().getPath() + "/" + nameOfFileItOPerates;

		return new AddVariableService(token, nameOfFileItOPerates);
	}

	@Override
	protected void assertServiceExecutedWithSuccess() {

		try {
			MyDriveService.getMyDrive().getFile(tokenFile);
			assert false;
		} catch (FileNotFoundException e) {
			// All went good
		}

	}

	@Test
	public void NewVariable()  {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste2", "/home/teste1/familia", new StrictlyTestObject());
		
		AddVariableService service = new AddVariableService(t1111, "var1", "varvalue1");
		service.execute();

		assertEquals("varvalue1", md.getVarValue("var1"));
	}
	
	@Test
	public void ExistingVariable()  {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste2", "/home/teste1/familia", new StrictlyTestObject());
		
		AddVariableService service = new AddVariableService(t1111, "var2", "varvalue3");
		service.execute();

		assertEquals("varvalue3", md.getVarValue("var1"));
	}
	
	@Test(expected = VarNotFoundException.class)
	public void NullName() throws MyDriveException  {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste2", "/home/teste1/familia", new StrictlyTestObject());
		
		AddVariableService service = new AddVariableService(t1111, null, "varvalue1");
		service.execute();

	}
	@Test(expected = InvalidValueException.class)
	public void NullValue() throws MyDriveException  {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste2", "/home/teste1/familia", new StrictlyTestObject());
		
		AddVariableService service = new AddVariableService(t1111, "var1", null);
		service.execute();

	}
}
