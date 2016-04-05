//package pt.tecnico.myDrive.service;
//
//import pt.tecnico.myDrive.domain.File;
//import pt.tecnico.myDrive.domain.PlainFile;
//import pt.tecnico.myDrive.exception.FileWithoutContentException;
//
//
//public class WriteFileService extends MyDriveService {
//
//	private long token;
//	private String name;
//	private String content;
//	
//	/**
//	 * Default Constructor
//	 */
//	public WriteFileService(long token, String name, String content) {
//		this.token = token;
//		this.name = name;
//		this.content = content;
//	}
//	
//	/**
//	 * Implementation of the service
//	 */
//	@Override
//	public final void dispatch() throws FileWithoutContentException{
//		//FIXME:TODO:XXX
//		File plainFile; //TODO get file on current directory with name.
//		if(!(plainFile instanceof PlainFile))
//			throw new FileWithoutContentException();
//		((PlainFile) plainFile).setContent(content);
//	}
//}
