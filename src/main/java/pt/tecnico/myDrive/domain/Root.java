package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.PrivateResourceException;

public class Root extends Root_Base {

	public Root() {
		super.setUsername("root");
		super.setSpecialPassword("***");
		super.setName("Super User");
		super.setPermissions("rwxdr-x-");
	}

	public Root(Element xml) {
		this.xmlImport(xml);
	}

	protected void xmlImport(Element xml) {
		super.xmlImport(xml);
	}	 

	@Override
	public boolean hasPermissions(File file, int position, String permissionLetter) {
		// System.out.println("\u001B[33;1mROOT using its SUPER
		// privileges\u001B[0m");
		return true;
	}

	@Override
	public void delete(User deleter) throws PrivateResourceException {
		System.out.println("\u001B[31mDeleting"+  this.getUsername() + "\u001B[0m");

		throw new PrivateResourceException("Tring to delete root user");
	}

	@Override
	protected long minutesSessionExpires() {
		return 10;
	}

	@Override
	public boolean specialPassUser() {
		return true;
	}
}
