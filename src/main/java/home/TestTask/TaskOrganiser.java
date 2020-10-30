package home.TestTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TaskOrganiser extends Thread{
	
	private TaskExecutor[] executors;
	private MemoryStructure memory;
	private String startingDirectory = DEFAULT_STARTING_DIRECTORY;
	public static final String DEFAULT_STARTING_DIRECTORY = "input/"; 
	
	private FileInputStream file;
	private InputStreamReader inputReader;
    private BufferedReader reader;
    
    private void initialize() {
    	memory = new MemoryStructure();
    	executors = new TaskExecutor[6];
    	for(int i = 0; i < 6; i++) {
    		executors[i] = new TaskExecutor();
    		executors[i].setName("Executor-" + i);
    		executors[i].connectToMemory(memory);
    	}
    	memory.start();
    	for(int i = 0; i < 6; i++)
    		executors[i].start();
    }
    
    private void terminate() {
    	for(int i = 0; i < 6; i++)
    		executors[i].execute(new TerminateCommand());
    	memory.terminate();
    }
    
    @Override
    public void run() {
    	initialize();
    	List<String> files = searchForFiles();
    	try {
	    	for(String fileName:files)
	    		readFile(fileName);
	    	sleep(100);
	    	terminate();
    	}catch(FileNotFoundException e) {
    		e.printStackTrace();
    	}catch(InterruptedException e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Method to change initial path to input files if necessary
     * @param path
     */
    public void setInputPath(String path) {
    	startingDirectory = path;
    }
    
    private List<String> searchForFiles() {
    	File folder = new File(startingDirectory);
    	List<String> fileNames = new ArrayList<String>();
    	File[] files = folder.listFiles();
    	for(File file:files) {
    		String fileName = file.getAbsolutePath();
    		if(fileName.contains(".csv"))
    			fileNames.add(fileName);
    	}
    	return fileNames;
    }
    
    //Could have a problem in Windows file system
    private void readFile(String filePath) throws FileNotFoundException {
    	String header, line = null;
        file = new FileInputStream(filePath);
        inputReader = new InputStreamReader(file);
        reader = new BufferedReader(inputReader);
        try {
        	header = reader.readLine();
        	sendHeader(header);
			int index = 0;
			do{
				line = reader.readLine();
				if(line != null) {
					executors[index].execute(new RecieveLineCommand(line));
					index = (index + 1) % 6;
				}
			}while(line != null);
			reader.close();
			inputReader.close();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void sendHeader(String header) {
    	for(int i = 0; i < 6; i++)
    		executors[i].execute(new RecieveHeaderCommand(header));
    }
}
