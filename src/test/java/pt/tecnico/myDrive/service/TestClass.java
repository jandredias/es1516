package pt.tecnico.myDrive.service;

import java.util.HashSet;
import java.util.Set;

/**
 * Used for app/file execution tests
 * 
 * @author ist169537
 *
 */
public class TestClass {

	private static boolean _ran = false;
	private static int _argsNum = 0;

	private static Set<String> _invocationArgs;

	public static void main(String args[]) {
		_argsNum = args.length;
		_ran = true;
		_invocationArgs = new HashSet<String>();
		for (String arg : args) {
			_invocationArgs.add(arg);
		}
	}

	public static boolean getRan() {
		return _ran;
	}

	public static void setRan(boolean ran) {
		_ran = ran;
	}

	public static int getArgsNum() {
		return _argsNum;
	}

	public static void setArgsNum(int argsNum) {
		_argsNum = argsNum;
	}

	public static Set<String> getInvocationArgs() {
		return _invocationArgs;
	}

	public static void setInvocationArgs(Set<String> invocationArgs) {
		_invocationArgs = invocationArgs;
	}

	public static void resetClass() {
		setRan(false);
		setArgsNum(0);
		setInvocationArgs(null);
	}

}
