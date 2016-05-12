package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.Variable;
import pt.tecnico.myDrive.exception.InvalidValueException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.TestSetupException;
import pt.tecnico.myDrive.exception.VarNotFoundException;
import pt.tecnico.myDrive.service.dto.VariableDto;

public class AddVariableTest extends TokenAccessTest {

	long token;
	String tokenFile;

	protected void populate() {
		MyDrive md = MyDrive.getInstance();

		try {
			md.addUser("teste1", "teste1234", "teste1", "rwxd----");
			long t1111 = md.getValidToken("teste1", "/home/teste1", new StrictlyTestObject());

			Variable var2 = new Variable();
			var2.setName("var2");
			var2.setValue("varvalue2");

			md.getSessionByToken(t1111).getVariablesSet().add(var2);

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
	public void NewVariable() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste1", "/home/teste1", new StrictlyTestObject());

		AddVariableService service = new AddVariableService(t1111, "var1", "varvalue1");
		service.execute();
		assertVariable(service.result(), "varvalue1", "var1");
	}

	@Test
	public void ExistingVariable() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste1", "/home/teste1", new StrictlyTestObject());

		AddVariableService service = new AddVariableService(t1111, "var2", "varvalue3");
		service.execute();
		assertVariable(service.result(), "varvalue3", "var2");
	}

	@Test(expected = VarNotFoundException.class)
	public void NullName() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste1", "/home/teste1", new StrictlyTestObject());

		AddVariableService service = new AddVariableService(t1111, null, "varvalue1");
		service.execute();

	}

	@Test(expected = InvalidValueException.class)
	public void NullValue() throws MyDriveException {
		MyDrive md = MyDrive.getInstance();
		long t1111 = md.getValidToken("teste1", "/home/teste1", new StrictlyTestObject());

		AddVariableService service = new AddVariableService(t1111, "var1", null);
		service.execute();

	}

	// private void assertVariable(Set<Variable> varSet, String expectedValue,
	// String name) {
	private void assertVariable(Set<VariableDto> varDtoSet, String expectedValue, String name) {
		boolean checked = false;
		// for (Variable var : varSet) {
		for (VariableDto varDto : varDtoSet) {
			if (varDto.getName().equals(name)) {
				if (!varDto.getValue().equals(expectedValue)) {
					fail("Values do not match: expected <" + expectedValue + ">, actual <" + varDto.getValue() + ">");
				} else {
					checked = true;
					break;
				}
			}
		}
		assertTrue(checked);
	}
}
