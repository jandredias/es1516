package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.PrivateResourceException;

public class Guest extends Guest_Base {

	public Guest() {
		super.setUsername("nobody");
		super.setPassword("");
		super.setName("Guest");
		super.setPermissions("rxwdr-x-");
	}

	public Guest(Element xml) {
		this.xmlImport(xml);
	}

	protected void xmlImport(Element xml) {
		super.xmlImport(xml);
	}
	
	public Element xmlExport() {
		Element element = super.xmlExport();

		element.setName("guest");

		return element;
	}
	
	@Override
	public boolean hasDeletePermissions(File file){
		return false;
	}
	@Override
	public boolean hasWritePermissions(File file){
		return false;
	}
	
	@Override
	public void delete(User deleter) throws PrivateResourceException{
		throw new PrivateResourceException("Tring to delete guest user");
	}
	
	@Override
	public void setPassword(String newPass) throws PrivateResourceException{
		throw new PrivateResourceException("Tring to change guest password");
	}

	@Override
	public boolean validateAccessTime(DateTime lastUsed){
		return true;
	}
}
