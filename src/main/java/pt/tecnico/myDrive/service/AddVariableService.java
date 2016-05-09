package pt.tecnico.myDrive.service;

import java.util.Set;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.Variable;
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
		if(name == null) {
			throw new VarNotFoundException("Variable name can't be null");
		}
		if(value == null) {
			throw new InvalidValueException("Value can't be null");
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
