package pt.tecnico.myDrive.presentation;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.CleanSessionService;

public class MyDriveShell extends Shell {

	private Map<String, Long> userTokens = new TreeMap<String, Long>(); //Map Used to store logged users and their tokens
	private long currentToken = 0;

	public long getCurrentToken() {
		return currentToken;
	}

	public void setCurrentToken(long newToken) {
		currentToken = newToken;
	}

	public long getTokenByUsername(String username) {
		return userTokens.get(username);
	}

	public String getUsernameByToken(long token) {
		for (java.util.Map.Entry<String, Long> entry : userTokens.entrySet()) {
			if (Objects.equals(token, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public void addUserToken(String username, long token) {
		userTokens.put(username, token);
	}

	public void removeGuest() {
		userTokens.remove("nobody");
	}

	public static void main(String[] args) throws Exception {
		MyDriveShell sh = new MyDriveShell();

		if (args.length >= 1)
			new Import(sh).execute(args); // IMport XML
		sh.execute();

	}

	public MyDriveShell() { // add commands here
		super("MyDrive");
		new LoginUser(this).execute(new String[] { "nobody", "" });
		new ChangeDirectory(this);
		new List(this);
		new Execute(this);
		new Write(this);
		new Environment(this);
		new Key(this);
		new AddFile(this);
		new Import(this);
		new MyDriveCommand(this, "quit", "Quit the command interpreter") {
			void execute(String[] args) {
				for (java.util.Map.Entry<String, Long> entry : userTokens.entrySet()) {
					try {
						new CleanSessionService(entry.getValue()).execute();
					} catch (MyDriveException e) {
						//USer does not exist
					}
				}
				
				System.out.println("MyDrive" + " quit");
				System.exit(0);
			}
		};
	}

}
