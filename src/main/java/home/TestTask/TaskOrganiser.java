package home.TestTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskOrganiser extends Thread{
	
	private TaskExecutor[] executors;
	private MemoryStructure memory;
	private ConcurrentLinkedQueue<Request> queue;
	private String startingDirectory = DEFAULT_STARTING_DIRECTORY;
	public static final String DEFAULT_STARTING_DIRECTORY = "input/";
	public static final int BUISY = 2, WAITING = 1;
	public int state;
	private boolean[] finishedTasks;
    
    private void initialize() {
    	setName("Organiser");
    	queue = new ConcurrentLinkedQueue<Request>();
    	memory = new MemoryStructure();
    	executors = new TaskExecutor[6];
    	finishedTasks = new boolean[6];
    	for(int i = 0; i < 6; i++) {
    		executors[i] = new TaskExecutor();
    		executors[i].index = i;
    		executors[i].setName("Executor-" + i);
    		executors[i].connectToMemory(memory);
    		executors[i].connectToOrganiser(this);
    	}
    	state = BUISY;
    	memory.start();
    	for(int i = 0; i < 6; i++)
    		executors[i].start();
    }
    
    @Override
    public void run() {
    	initialize();
    	List<String> files = searchForFiles();
    	int index = 0;
    	while(!allExecutorsFinished()) {
    		switch(state) {
    		case BUISY:
    			try {
					readFile(files.get(index));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
    			index++;
    			if(index >= files.size()) {
        			state = WAITING;
    			}
    			break;
    		case WAITING:
    			while((!allExecutorsFinished())&&(!queue.isEmpty())) {
    				Request request = queue.poll();
        			switch(request.identify()) {
        			case Request.FINISHED_TASK_REQUEST:
        				FinishedTaskRequest finishedTask = (FinishedTaskRequest)request;
        				finishedTasks[finishedTask.executorIndex] = true;
        				executors[finishedTask.executorIndex].recieve(new TerminateRequest());
            			break;
        			case Request.CONFIRMATION:
        				Confirmation confirmation = (Confirmation)request;
        				int state = executors[(confirmation).executorIndex].state;
        				if(state != TaskExecutor.TERMINATING)
        					executors[(confirmation).executorIndex].state = TaskExecutor.BUISY;
        			}
    			}
    		}
    	}
    	while(true) {
    		boolean ready = true;
    		for(TaskExecutor executor:executors)
    			if(executor.getState() != Thread.State.TERMINATED) {
    				ready = false;
    				break;
    			}
    		if(ready)
    			break;
    	}
	    memory.terminate();
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
    	String header, line = "";
    	FileInputStream file = new FileInputStream(filePath);
        InputStreamReader inputReader = new InputStreamReader(file);
        BufferedReader reader = new BufferedReader(inputReader);
        try {
        	header = reader.readLine();
        	sendHeader(header);
        	int forcefulSendingIndex = 0;
        	do {
        		if(queue.isEmpty()) {
        			line = reader.readLine();
        			if(line != null) {
        				boolean sent = false;
        				//Search for undiscovered unused executors
        				for(TaskExecutor executor:executors)
        					if(executor.state == TaskExecutor.WAITING) {
        						System.out.println(getName() + ": requesting unoccupied executor:" + executor.getName());
        						executor.recieve(new RecieveLineRequest(line));
        						sent = true;
        						break;
        					}
        				//Force discovered line on next Executor
        				if(!sent) {
        					System.out.println(getName() + ": All Executors buisy, forceing request on: " + executors[forcefulSendingIndex].getName());
        					executors[forcefulSendingIndex].recieve(new RecieveLineRequest(line));
        					forcefulSendingIndex = (forcefulSendingIndex + 1) % 6;
        				}
        			}
        		}
        		//Handle queued requests
        		while(!queue.isEmpty()) {
        			Request request = queue.poll();
        			switch(request.identify()) {
        			case Request.FINISHED_TASK_REQUEST:
        				FinishedTaskRequest finishedTask = (FinishedTaskRequest)request;
            			System.out.println(getName() + ": recieving request from: Executor-" + finishedTask.executorIndex);
            			line = reader.readLine();
            			if(line != null)
            				executors[finishedTask.executorIndex].recieve(new RecieveLineRequest(line));
            			break;
        			case Request.CONFIRMATION:
        				Confirmation confirmation = (Confirmation)request;
        				int state = executors[(confirmation).executorIndex].state;
        				if(state != TaskExecutor.TERMINATING)
        					executors[(confirmation).executorIndex].state = TaskExecutor.BUISY;
        			}
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
    		executors[i].recieve(new RecieveHeaderRequest(header));
    }
    
    public void recieveRequest(Request request) {
    	queue.add(request);
    }
    
    private boolean allExecutorsFinished() {
    	boolean answer = true;
    	for(boolean executorState:finishedTasks)
    		if(!executorState) {
    			answer = false;
    			break;
    		}
    	return answer;
    }
}
