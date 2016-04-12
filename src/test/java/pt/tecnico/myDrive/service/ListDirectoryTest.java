package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import pt.tecnico.myDrive.domain.Application;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.StrictlyTestObject;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.TestSetupException;


//public class ListDirectoryTest extends TokenServiceTest {

public class ListDirectoryTest extends PermissionsTest {

	private ListDirectoryService listDirService;
	/**
	 * Method that runs before each @Test
	 */
	protected void populate() {
		String username	= "Joao"; 
		String folder 	= "TestDir";
		try{
			MyDrive md = MyDriveService.getMyDrive();
			md.addUser(username,username,username,"rwxdrwxd");
			User user = md.getUserByUsername(username);
			md.addDirectory("/home/" + username, folder, user);
		}
		catch(MyDriveException E){
			throw new TestSetupException("ListDirectoryService: Populate");
		}
		
		StrictlyTestObject dummyObject = new StrictlyTestObject(); 
		long token = MyDriveService.getMyDrive().getValidToken(username,"/home/" + username + "/" + folder, dummyObject);
		listDirService = new ListDirectoryService(token);
	}

	

	/* ---------------------------------------------------------------------- */
	/* ------------------------ Permissions Related ------------------------- */
	@Override
	protected MyDriveService createService(long token, String nameOfFileItOPerates) {
		return new ListDirectoryService(token);
	}
	
	@Override
	protected char getPermissionChar() {
		return 'r';
	}
	
	@Override
	protected void assertServiceExecutedWithSuccess(){
		listDirService = (ListDirectoryService) abstractClassService; //From Upper class
		assertNotNull(listDirService.result());
	}
	/* ------------------------ Permissions Related ------------------------- */
	/* ---------------------------------------------------------------------- */


	/**
	 * Method that checks a @param filesList has @param numberOfFiles and
	 * every field is not null
	 * 
	 * @param filesList
	 * @param numberOfFiles
	 */
	private void checkNotNullWithNFiles(List<List<String>> filesList, int numberOfFiles){
		assertEquals(numberOfFiles, filesList.size());
		for (List<String> individualFileList : filesList){
			assertEquals("each file must have 7 fields",7,individualFileList.size());
			for (String field : individualFileList ){
				assertNotNull("fields must not be null", field);
				assertTrue("fields must not be empty", !(field.equals("")));
			}
		}
	}
	
	/**
	 * Checks that every @param file is listed correctly in @param fileList
	 * @param file
	 * @param fileList
	 * @param FileType
	 */
	private void checkSingleFileStuff(File file, List<String> fileList, String FileType){

		String listedFileType = fileList.get(0);
		assertEquals("file type",listedFileType, FileType );

		String listedPermissions = fileList.get(1);
		assertEquals("permissions ",listedPermissions , file.getPermissions() );

		//Dimension has its own special Tests;

		String listedUsername = fileList.get(3);
		assertEquals("username",listedUsername, file.getOwner().getUsername());

		//TODO:: Check letters COMPATIBILITY
		String listedId = fileList.get(4);
		assertEquals("id",listedId, file.getId());

		String listedDate = fileList.get(5);
		assertEquals("date",listedDate, file.getModification().toString());

		String listedName = fileList.get(6);
		assertEquals("name ",listedName, file.getName());
	}

	/* ---------------------------------------------------------------------- */
	/* ----------------------------- Tests ---------------------------------- */
	@Test
	public void listEmptyDirectory() throws MyDriveException{

		listDirService.execute();

		List<List<String>> resultList = listDirService.result();
		checkNotNullWithNFiles(resultList, 2);

		String fileType = resultList.get(0).get(0);
		assertEquals("Only Directorys on empty dir", fileType, "Directory");
		fileType = resultList.get(1).get(0);
		assertEquals("Only Directorys on empty dir", fileType, "Directory");

		String name			= resultList.get(0).get(6);
		assertEquals("second file must be named .", name, ".");
		name			= resultList.get(1).get(6);
		assertEquals("second file must be named ..", name, "..");
	}

	@Test
	public void listDirectoryWithPlainFile() throws MyDriveException{

		MyDrive md = MyDriveService.getMyDrive(); 
		User joao = md.getUserByUsername("joao");
		md.addPlainFile("/home/joao/TestDir", "PlainFile", joao, "content");

		listDirService.execute();

		List<List<String>> serviceList = listDirService.result();
		checkNotNullWithNFiles(serviceList, 3);

		List<String> plainFileList = serviceList.get(2);
		PlainFile file = ( PlainFile ) md.getFile("/home/joao/TestDir/PlainFile");
		checkSingleFileStuff(file, plainFileList, "Plain File");

		//TODO:: Check letters dont crash
		String listedDimension	= plainFileList.get(2);	
		assertEquals("dimension",listedDimension, file.getContent().length());
	}

	@Test
	public void listDirectoryWithDirectory() throws MyDriveException{

		MyDrive md = MyDriveService.getMyDrive(); 
		User joao = md.getUserByUsername("joao");
		md.addDirectory("/home/joao/TestDir", "Directory", joao);

		listDirService.execute();

		List<List<String>> serviceList = listDirService.result();
		checkNotNullWithNFiles(serviceList, 3);

		List<String> directoryList = serviceList.get(2);
		Directory file = ( Directory ) md.getFile("/home/joao/TestDir/Directory");
		checkSingleFileStuff(file, directoryList , "Directory");

		//Dimension has its own special tests
	}

	@Test
	public void listDirectoryWithlink() throws MyDriveException{

		MyDrive md = MyDriveService.getMyDrive(); 
		User joao = md.getUserByUsername("joao");
		md.addLink("/home/joao/TestDir", "Link", joao, "linkContent");

		listDirService.execute();

		List<List<String>> serviceList = listDirService.result();
		checkNotNullWithNFiles(serviceList, 3);

		List<String> linkList = serviceList.get(2); 
		Link  file = ( Link ) md.getFile("/home/joao/TestDir/Link");
		checkSingleFileStuff(file, linkList , "Link"); 

		//TODO:: Check letters dont crash
		String listedDimension	= linkList.get(2);	
		assertEquals("dimension",listedDimension, file.getContent().length());
	}

	@Test
	public void listDirectoryWithApp() throws MyDriveException{

		MyDrive md = MyDriveService.getMyDrive(); 
		User joao = md.getUserByUsername("joao");
		md.addApplication("/home/joao/TestDir", "App", joao, "AppCOntent"); 

		listDirService.execute();

		List<List<String>> serviceList = listDirService.result();
		checkNotNullWithNFiles(serviceList, 3);

		List<String> applicationList = serviceList.get(2);
		Application file = ( Application ) md.getFile("/home/joao/TestDir/App");
		checkSingleFileStuff(file, applicationList , "Application");

		//TODO:: Check letters dont crash
		String listedDimension	= applicationList.get(2);	
		assertEquals("dimension",listedDimension, file.getContent().length());
	}

	@Test
	public void listDirectoryWith6FilesAlphabeticly() throws MyDriveException{

		MyDrive md = MyDriveService.getMyDrive(); 
		User joao = md.getUserByUsername("joao");
		md.addPlainFile("/home/joao/TestDir", "PlainFile", joao, "AppCOntent"); 
		md.addApplication("/home/joao/TestDir", "App", joao, "AppCOntent"); 
		md.addLink("/home/joao/TestDir", "Link", joao, "AppCOntent"); 
		md.addDirectory("/home/joao/TestDir", "Dir2", joao); 
		md.addDirectory("/home/joao/TestDir", "Dir1", joao); 
		md.addApplication("/home/joao/TestDir", ".App534", joao, "AppCOntent"); 
		
		ArrayList<String> createdNamesList = new ArrayList<String>();
		createdNamesList.add(0, ".");
		createdNamesList.add(1, "..");
		createdNamesList.add(2, "PlainFile");
		createdNamesList.add(3, "App");
		createdNamesList.add(4, "Link");
		createdNamesList.add(5, "Dir2");
		createdNamesList.add(6, "Dir1");
		createdNamesList.add(7, ".App534");
		
		Collections.sort(createdNamesList);
		listDirService.execute();
		
		List<List<String>> serviceList = listDirService.result();
		checkNotNullWithNFiles(serviceList, 2 + 6);

		ArrayList<String> receivedNamesList = new ArrayList<String>();
		for (List<String> fileList : serviceList ){
			receivedNamesList.add(-1, fileList.get(6));
		}
		
		for (int index = 0 ; index < receivedNamesList.size(); index++){
			assertEquals(createdNamesList.get(index), receivedNamesList.get(index));
		}
	}

	/* ---------------------------------------------------------------------- */
	/* -------------------------- Dimension Tests --------------------------- */	
	/**
	 * Method that tests plainFile is Listed with dimension of size @param contentSize 
	 * @param contentSize
	 * @throws MyDriveException
	 */
	private void plainFileDimension(int contentSize) throws MyDriveException{
		String content = StringUtils.repeat("t", contentSize);

		MyDrive md = MyDriveService.getMyDrive(); 
		User joao = md.getUserByUsername("joao");
		md.addPlainFile("/home/joao/TestDir", "Plain", joao, content);
		
		listDirService.execute();

		List<List<String>> serviceList = listDirService.result();
		checkNotNullWithNFiles(serviceList, 3);
		
		List<String> plainFileList = serviceList.get(2);

		String listedDimension	= plainFileList.get(2);	
		assertEquals("dimension",listedDimension, contentSize);
	}
	
	@Test
	public void emptyPlainFileDimension() throws MyDriveException{
		plainFileDimension(0);
	}

	@Test
	public void char_1_PlainFileDimension() throws MyDriveException{
		plainFileDimension(1);
	}

	@Test
	public void char_1000_PlainFileDimension() throws MyDriveException{
		plainFileDimension(1000);
	}

	/**
	 * Method that tests plainFile is Listed with dimension of size @param numberOfFiles
	 * @param numberOfFiles
	 * @throws MyDriveException
	 */
	private void directoryDimension(int numberOfFiles) throws MyDriveException{

		MyDrive md = MyDriveService.getMyDrive(); 
		User joao = md.getUserByUsername("joao");
		
		for(int i = 0; i < numberOfFiles ; i++ )
			md.addPlainFile("/home/joao/TestDir", "PlainFile"+i, joao, "");
		
		listDirService.execute();

		List<List<String>> serviceList = listDirService.result();
		checkNotNullWithNFiles(serviceList, 2 + numberOfFiles);
		
		List<String> directoryList = serviceList.get(2);

		String listedDimension	= directoryList.get(2);	
		assertEquals("dimension",listedDimension, numberOfFiles + 2);
	}
	
	@Test
	public void emptyDirectoryDimension() throws MyDriveException{
		directoryDimension(0);
	}

	@Test
	public void files_1_DirectoryDimension() throws MyDriveException{
		directoryDimension(1);
	}
	
	@Test
	public void files_7_DirectoryDimension() throws MyDriveException{
		directoryDimension(7);
	}
	/* ----------------------------- Tests ---------------------------------- */
	/* ---------------------------------------------------------------------- */

}
