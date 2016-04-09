package pt.tecnico.myDrive.service;

import org.junit.Test;

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