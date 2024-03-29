package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.PrivateResourceException;

public class Guest extends Guest_Base {

	public Guest() {
		super.setUsername("nobody");
		super.setSpecialPassword("");
		super.setName("Guest");
		super.setPermissions("rwxdr-x-");
	}

	public Guest(Element xml) {
		this.xmlImport(xml);
	}

	protected void xmlImport(Element xml) {
		super.xmlImport(xml);
	}

	/*
	 * @Override public Element xmlExport() {return null;}
	 */

	@Override
	public boolean hasDeletePermissions(File file) {
		return false;
	}

	@Override
	public boolean hasWritePermissions(File file) {
		return false;
	}

	@Override
	public void delete(User deleter) throws PrivateResourceException {
		System.out.println("\u001B[33mDeleting "+  this.getUsername() + "\u001B[0m");

		throw new PrivateResourceException("Tring to delete guest user");
	}

	@Override
	public void setPassword(String newPass) throws PrivateResourceException {
		throw new PrivateResourceException("Tring to change guest password");
	}

	@Override
	public boolean validateAccessTime(DateTime lastUsed) {
		return true;
	}

	@Override
	public boolean specialPassUser() {
		return true;
	}
}
