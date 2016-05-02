package pt.tecnico.myDrive.domain;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import pt.tecnico.myDrive.exception.AppExecutionException;
import pt.tecnico.myDrive.exception.UnsupportedOperationException;

public class ExecuteFileVisitor implements Visitor{

	private String[] args;
	
	public ExecuteFileVisitor() {
		resetArgs();
	}
	public ExecuteFileVisitor(String[] inArgs) {
		this.args = inArgs;
	}

	
	private void resetArgs() {
		args = new String[]{};		
	}
	
	public void visitPlainFile(PlainFile p) throws UnsupportedOperationException{
		String content 	= p.getContent();
		String[] lines	= content.split("\n");
		
		for(String line : lines){
			String[] executingCommand = line.split(" ");
			args = Arrays.copyOfRange(executingCommand, 1, executingCommand.length);
			System.out.println("\u001B[32mTODO:\u001B[0m Execute app on path: " + executingCommand[0] + " with args: " + args);
			//App app = getApp(executingCommand[0]);
			//app.accept(this);
		}
		throw new RuntimeException("\u001B[1;31mFIXME: ExecuteFileService: NOT IMPLEMENTED visitPlainFile\u001B[0m");
	}

	public void visitApplication(Application app) throws UnsupportedOperationException, AppExecutionException{
		String content 	= app.getContent();
		Class[] argTypes = new Class[] { String[].class };
		Method method ;
		try{
			int count = StringUtils.countMatches(content, ".");
			if(count == 1){
				Class<?> cls = Class.forName(content);
				method = cls.getDeclaredMethod("main", argTypes);
			} 
			else{
				try {
					//Test case pack1.pack2.(pack3.).class   //Executes main
					Class<?> cls = Class.forName(content);
					method = cls.getDeclaredMethod("main", argTypes);
				} catch (ClassNotFoundException e) {
					//Test case pack1.pack2.class.method
					int lastDot = content.lastIndexOf("\\.");
					String className = content.substring(0,lastDot);
					String methodName= content.substring(lastDot);
					Class<?> cls = Class.forName(className);
					
					method = cls.getDeclaredMethod(methodName, argTypes);
				}
			}
			method.invoke(null, (Object)this.args);
			resetArgs();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			// TODO Not Sure What to do here..
			e.printStackTrace();
			throw new RuntimeException("\u001B[1;31mFIXME: ExecuteFileService: NOT IMPLEMENTED\u001B[0m");
		} catch (NoSuchMethodException e) {
			throw new AppExecutionException("Method not found: " + e.getMessage());
		}  catch(ClassNotFoundException e) {
			throw new AppExecutionException("Class not found: " + e.getMessage());
		}
	}

	public void visitDirectory(Directory d) throws UnsupportedOperationException{
		throw new UnsupportedOperationException("Can't execute Directory.");
	}
	public void visitFile(File f) throws UnsupportedOperationException{ 
		throw new UnsupportedOperationException("Can't execute File."); 
	}
	public void visitLink(Link l) throws UnsupportedOperationException{
		throw new UnsupportedOperationException("\u001B[31;1mCan't execute Link: Probably domain is wrong!\u001B[0m"); 
	}
	public void visitUser(User u) throws UnsupportedOperationException{
		throw new UnsupportedOperationException("Can't execute User."); 
	}
	public void visitRoot(Root r) throws UnsupportedOperationException{
		throw new UnsupportedOperationException("Can't execute Root."); 
	}
	public void visitMyDrive(MyDrive mD) throws UnsupportedOperationException{
		throw new UnsupportedOperationException("Can't execute MyDrive."); 
	}
}
