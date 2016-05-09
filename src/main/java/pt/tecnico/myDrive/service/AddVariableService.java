package pt.tecnico.myDrive.service;

import java.util.Set;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.Variable;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.InvalidValueException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.VarNotFoundException;

public class AddVariableService extends MyDriveService {

	private MyDrive myDrive;

	private String name;
	private String value;
	private long token;

	private Variable variable;
	private Session session;

	public AddVariableService(long token, String name, String value) {
		this.token = token;
		this.name = name;
		this.value = value;

		myDrive = MyDrive.getInstance();
		session = myDrive.getSessionByToken(token);
	}

	public AddVariableService(long token, String name) {
		this(token, name, null);
	}

	public AddVariableService(long token) {
		this(token, null);
	}

	@Override
	protected void dispatch() throws MyDriveException {
		if (name == null) {
			throw new VarNotFoundException("Variable name can't be null");
		}
		if (value == null) {
			throw new InvalidValueException("Value can't be null");
		}
		if (session.getUser().getUsername().equals("root")
				&& DateTime.now().minusMinutes(10).getMillis() >= session.getLastUsed().getMillis()) {
			throw new InvalidTokenException("Token has expired");
		}
		if (DateTime.now().minusHours(2).getMillis() >= session.getLastUsed().getMillis()) {
			throw new InvalidTokenException("Token has expired");
		}

		Set<Variable> varSet = session.getVariablesSet();

		for (Variable var : varSet) {
			if (var.getName().equals(name)) {
				var.setValue(value);
				return;
			}
		}

		variable = new Variable();
		variable.setName(name);
		variable.setValue(value);

		varSet.add(variable);

	}

	public final Set<Variable> result() {
		return myDrive.getSessionByToken(token).getVariablesSet();
	}

}
