package home.TestTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskOrganiser extends Thread{
	
	private MemoryStructure memory;
	private String startingDirectory = "/input";
	private ThreadPoolExecutor pool;
    
    public TaskOrganiser() {
    	setName("Organizer");
    	memory = new MemoryStructure();
    	pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    	pool.setKeepAliveTime(10, TimeUnit.MICROSECONDS);
    }
    
    /**
     * Method to change initial path to input files if necessary
     * @param path
     */
    public void setInputPath(String path) {
    	startingDirectory = path;
    }
    
    @Override
    public void run() {
    	memory.start();
    	File[] files = searchForFiles();
    	for(File file:files) {
			try {
				ReadFileTask task;
				task = new ReadFileTask(file);
				task.connectToMemory(memory);
	    		pool.execute(task);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	pool.allowCoreThreadTimeOut(true);
    	memory.terminate();
    }
    
    private File[] searchForFiles() {
    	File folder = new File(startingDirectory);
    	File[] files = folder.listFiles(), inputFiles = new File[files.length];
    	int currentIndex = 0;
    	for(File file:files)
    		if(file.getAbsolutePath().contains(".csv")) {
    			inputFiles[currentIndex] = file;
    			currentIndex++;
    		}
    	
    	files = new File[currentIndex];
    	for(int i = 0; i < currentIndex; i++)
    		files[i] = inputFiles[i];
    	return files;
    }
}
