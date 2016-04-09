package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

public abstract class TokenAccessTest extends AbstractServiceTest{
	
	protected abstract void populate(); 

	protected abstract MyDriveService createTokenService(long token);

	
	@Test
	//tokenAccessClass: Mock example, not Implemented FIXME:TODO:XXX
	public void test1(){
		System.out.println("\u001B[32;1m" + "TEST1"	+ "\u001B[0m");
		long token = 0;
		MyDriveService service = createTokenService(token);
	}
	//More TESTS tokenAccessClass: FIXME:TODO:XXX
}