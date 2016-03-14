package pt.tecnico.myDrive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.joda.time.DateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exception.DirectoryIsNotEmptyException;
import pt.tecnico.myDrive.exception.FileExistsException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;
import pt.tecnico.myDrive.exception.NoSuchUserException;
import pt.tecnico.myDrive.exception.UsernameAlreadyInUseException;

public class MyDriveApplication {
  static final Logger log = LogManager.getRootLogger();

  public static void main(String[] args) throws IOException {
    //TODO
    try {
      //init();
      //for (String s: args) xmlScan(new File(s));

      setup();
      xmlPrint();
      //teste_tiago();
      //listDirectoryTest();

    } finally { FenixFramework.shutdown(); }

  }

  @Atomic
  public static void init() {
    log.trace("Init: " + FenixFramework.getDomainRoot());
    MyDrive.getInstance().cleanup();
  }

  @Atomic
  public static void setup() {
    log.trace("Setup: " + FenixFramework.getDomainRoot());


    MyDrive md = MyDrive.getInstance();
    log.trace("Setup: Create MyDrive");

    //1

    User rootUsr = md.getRootUser();
    Directory rootDir = md.getRootDirectory();
    Directory homeDir;
    try {
      homeDir = rootDir.getDirectory("home");
    } catch (FileNotFoundException e2) {
      try {
		homeDir = new Directory("home", md.getFileId(), new DateTime(), 11111010, rootUsr, rootDir);
      } catch (FileExistsException e) {
			/* Impossible case */
			log.error("IMPOSSIBLE CASE ABORTING OPERATION");
			return;
      }
      return;
    };
    try {
		new PlainFile("README", md.getFileId(), new DateTime(), 11111011, md.getRootUser(), "lista de utilizadores", homeDir);
	} catch (FileExistsException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}

    //2
    Directory usr;
	try {
		usr = new Directory("usr", md.getFileId(), new DateTime(), 11111010, rootUsr, rootDir);
	} catch (FileExistsException e2) {
		try {
			usr = rootDir.getDirectory("usr");
		} catch (FileNotFoundException e) {
			/* Impossible case */
			log.error("IMPOSSIBLE CASE ABORTING OPERATION");
			return;
		}

	}
    Directory local;
	try {
		local = new Directory("local", md.getFileId(), new DateTime(), 11111010, rootUsr, usr);
	} catch (FileExistsException e2) {
		try {
			local = rootDir.getDirectory("local");
		} catch (FileNotFoundException e) {
			/* Impossible case */
			log.error("IMPOSSIBLE CASE ABORTING OPERATION");
			return;
		}
	}

    try {
		new Directory("bin", md.getFileId(), new DateTime(), 11111010, rootUsr, local);
	} catch (FileExistsException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}

    //3
    try {
      System.out.println("File contents: " + md.getFileContents("/home/README"));
    } catch (FileNotFoundException | NotDirectoryException | UnsupportedOperationException e) {
      log.debug("Caught exception while obtaining file contents");
      e.printStackTrace();
    }

    //4
    try{
      md.removeFile("/usr/local/bin");
    }
    catch (DirectoryIsNotEmptyException e) {
      log.error("Cannot delete a non-empty folder");
    }
    catch (FileNotFoundException e){
      log.error("The file doesn't exist");
    }
    catch (NotDirectoryException e) {
      //Should never occur
      log.error("The father directory isn't a directory");
    }


    //6
    try{
      md.removeFile("/home/README");
    }
    catch (DirectoryIsNotEmptyException e) {
      log.error("Cannot delete a non-empty folder");
    }
    catch (FileNotFoundException e){
      log.error("The file doesn't exist");
    }
    catch (NotDirectoryException e) {
      //Should never occur
      log.error("The father directory isn't a directory");
    }

    //7
    try {
      System.out.println("Directory Listing /home: " + md.listDir("/home"));
    } catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }


    //TODO EXTRA WORK.. probably delete from this point onwards
    //8
    try {
      System.out.println("Directory Listing /: " + md.listDir("/"));
    } catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    //9
    System.out.println("Deleting /usr");
    try {
      md.removeFile("/usr");
    } catch (DirectoryIsNotEmptyException e) {
      System.out.println("ERROR: Directory not empty");
    } catch (NotDirectoryException | FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //7
    try {
      String folder = "/";
      System.out.println("Directory Listing "+folder+" : " + md.listDir(folder));
    } catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    //7
    try {
      String folder = "/usr";
      System.out.println("Directory Listing "+folder+" : " + md.listDir(folder));
    } catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    try {
      String folder = "/usr/";
      System.out.println("Directory Listing "+folder+" : " + md.listDir(folder));
    } catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    //10
    try {
      System.out.println("Deleting /usr/local");
      md.removeFile("/usr/local");
    } catch (DirectoryIsNotEmptyException e) {
      System.out.println("ERROR: Directory not empty");
    } catch (NotDirectoryException | FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //7
    try {
      String folder = "/usr";
      System.out.println("Directory Listing "+folder+" : " + md.listDir(folder));
    } catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    try {
      String folder = "/usr/local";
      System.out.println("Directory Listing "+folder+" : " + md.listDir(folder));
    } catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
      System.out.println("ERROR (EXPECTED & INTENDED): FileNotFound : " + "/usr/local");
    }
    //10
    try {
      System.out.println("Deleting /usr");
      md.removeFile("/usr");
    } catch (DirectoryIsNotEmptyException e) {
      System.out.println("ERROR: Directory not empty");
    } catch (NotDirectoryException | FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    try {
      String folder = "/";
      System.out.println("Directory Listing "+folder+" : " + md.listDir(folder));
    } catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }


	try {
		String folder = "/";
		System.out.println("Directory Listing "+folder+" : " + md.listDir(folder));
	} catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

	try {
		md.addUser("miguel");
	} catch (InvalidUsernameException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UsernameAlreadyInUseException e) {
		// TODO Auto-generated catch block
		System.out.println("duplicate user miguel");
	}

	try {
		md.addUser("miguel");
	} catch (InvalidUsernameException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UsernameAlreadyInUseException e) {
		System.out.println("ERROR (EXPECTED & INTENDED): duplicate user miguel");
	}
    try {
        System.out.println("Deleting /home/miguel");
        md.removeFile("/home/miguel");
      } catch (DirectoryIsNotEmptyException e) {
        System.out.println("ERROR: Directory not empty");
      } catch (NotDirectoryException | FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }


	try {
		String folder = "/home";
		System.out.println("Directory Listing "+folder+" : " + md.listDir(folder));
	} catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

	// 11
	try {
		md.getFileContents("/home");
		log.error("Should have thrown exception");
	} catch (UnsupportedOperationException e) {
		log.debug("Thrown exception when trying to get the contents of a directory (expected)");
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NotDirectoryException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	//10
	try {
		System.out.println("Deleting /home/miguel");
		md.removeFile("/home/miguel");
	} catch (DirectoryIsNotEmptyException e) {
		System.out.println("ERROR: Directory not empty");
	} catch (NotDirectoryException | FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	try {
		String folder = "/";
		System.out.println("Directory Listing "+folder+" : " + md.listDir(folder));
	} catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	try {
		String folder = "/home/../home/.";
		System.out.println("Directory Listing "+folder+" : " + md.listDir(folder));
	} catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	log.debug("Setup Complete");
 }


	/*
	* Prints a XML output to console
	*/
	@Atomic
	public static void xmlPrint() {
	  log.trace("xmlPrint: " + FenixFramework.getDomainRoot());
	  MyDrive md = MyDrive.getInstance();
	  Document doc = md.xmlExport();
	  XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
	  try {
	    xmlOutput.output(doc, new PrintStream(System.out));
	  } catch (IOException e) {
	    System.out.println(e);
	  }
	}

	/**
	* Reads a XML file and imports the content to the file system
	*
	* @param File
	*/
	@Atomic
	public static void xmlScan(File file) {
	  log.trace("xmlScan: " + FenixFramework.getDomainRoot());

	  MyDrive md = MyDrive.getInstance();
	  SAXBuilder builder = new SAXBuilder();
	  try {
	    log.trace("xmlScan: Importing the main document");
	    Document document = (Document)builder.build(file);
	    md.xmlImport(document.getRootElement());
	  } catch(JDOMException | IOException e) {
	    e.printStackTrace();
	  } catch(InvalidUsernameException e){
	    System.out.println("Invalid username on XML import file");
	  } catch(FileNotFoundException e){
	    System.out.println("File not found on XML import file");
	  } catch(NotDirectoryException e) {
	    System.out.println("Only directories can contain another files");
	  } catch(NoSuchUserException e) {
	    System.out.println("There is not such a user on XML import file");
	  }catch(FileExistsException e){
	    System.out.println("Duplicated file on XML import file");
	  }

	  log.trace("End of xmlScan");
	}
	
	@Atomic
	public static void listDirectoryTest() {

	  try {

		  MyDrive md = MyDrive.getInstance();

		  User rootUsr = md.getRootUser();
		  Directory rootDir = md.getRootDirectory();
		  Directory testDir = new Directory("testDir", md.getFileId(), new DateTime(), 11111010, rootUsr, rootDir);
		  new Directory("testDir2", md.getFileId(), new DateTime(), 11111010, rootUsr, testDir);
		  new Directory("testDir3", md.getFileId(), new DateTime(), 11111010, rootUsr, rootDir);
		  log.debug(md.listDir("/testDir"));

	  } catch (FileExistsException e) {
	    e.printStackTrace();
	  }
	  catch (UnsupportedOperationException e) {
	    e.printStackTrace();
	  }
	  catch (FileNotFoundException e) {
	    e.printStackTrace();
	  }
	  catch (NotDirectoryException e) {
	    e.printStackTrace();
	  }

	}

	@Atomic
	public static void teste_tiago(){
		log.debug("TIAGOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
		MyDrive md = MyDrive.getInstance();
		Directory rootDir = md.getRootDirectory();
		User rootUsr = md.getRootUser();

		Directory olaDir;
		try {
			olaDir = new Directory("ola12345678", md.getFileId(), new DateTime(), 11111010, rootUsr, rootDir);
		} catch (FileExistsException e2) {
			try {
				olaDir = rootDir.getDirectory("ola12345678");
			} catch (FileNotFoundException e) {
				/* Impossible case */
				log.error("IMPOSSIBLE CASE ABORTING OPERATION");
				return;
			}
		}

		PlainFile file = null;
		try {
			file = new PlainFile("README", md.getFileId(), new DateTime(), 11111011, rootUsr, "cenas", olaDir);
		} catch (FileExistsException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		try{
			md.removeFile("/");
			}
			catch (FileNotFoundException e) {
				log.debug("Couldn't find /");
			}
			catch (DirectoryIsNotEmptyException e){
				log.debug("This should not occur, / is empty");
			}
			catch (NotDirectoryException e) {
				e.printStackTrace();
			}

		try {
			String folder = "/";
			System.out.println("Directory Listing "+folder+" : " + md.listDir(folder));
		} catch (UnsupportedOperationException | FileNotFoundException | NotDirectoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
			olaDir.addChildFile(file);
		}
		catch (FileExistsException e){
			log.debug("Trying to add file that already exists");
		}
		try{
		md.removeFile("/ola12345678");
		}
		catch (FileNotFoundException e) {
			log.debug("Couldn't find ola12345678");
		}
		catch (DirectoryIsNotEmptyException e){
			log.debug("This should occur, ola12345678 is not empty");
		}
		catch (NotDirectoryException e) {
			e.printStackTrace();
		}

		try{
			md.removeFile("/ola12345678/README");
			}
			catch (FileNotFoundException e) {
				log.debug("Couldn't find readme");
			}
			catch (DirectoryIsNotEmptyException e){
				log.debug("This shouldn't occur, readme is a file");
			}
			catch (NotDirectoryException e) {
				e.printStackTrace();
			}
		Directory olaDir1 = null;
		try {
			olaDir1 = new Directory("ola12345678", md.getFileId(), new DateTime(), 11111010, rootUsr, rootDir);
		} catch (FileExistsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
			olaDir.addChildFile(olaDir1);
			}
			catch (FileExistsException e){
				log.debug("Trying to add file that already exists");
			}

		try{
			md.removeFile("/ola12345678/ola12345678");
			}
			catch (FileNotFoundException e) {
				log.debug("Couldn't find /ola12345678/ola12345678");
			}
			catch (DirectoryIsNotEmptyException e){
				log.debug("This should not occur, /ola12345678/ola12345678 is empty");
			}
			catch (NotDirectoryException e) {
				e.printStackTrace();
			}
		try{
			md.removeFile("ola12345678/ola12345678");
			}
			catch (FileNotFoundException e) {
				log.debug("This should occur, doesn't have /");
			}
			catch (DirectoryIsNotEmptyException e){
				log.debug("This shouldn't occur, ola12345678/ola12345678");
			}
			catch (NotDirectoryException e) {
				e.printStackTrace();
			}



		log.debug("TIAGOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
	  }
	}
