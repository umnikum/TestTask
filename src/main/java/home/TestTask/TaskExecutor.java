package home.TestTask;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskExecutor extends Thread{
	
	private ConcurrentLinkedQueue<Request> queue;
	private String[] header;
	private MemoryStructure memory;
	private TaskOrganiser organiser;
	public int state, index;
	public static final int BUISY = 2, WAITING = 1, TERMINATING = 0;
	
	
	public TaskExecutor() {
		queue = new ConcurrentLinkedQueue<Request>();
		state = WAITING;
	}
	
	public void connectToMemory(MemoryStructure memoryLink) {
		memory = memoryLink;
	}
	
	public void connectToOrganiser(TaskOrganiser organiser) {
		this.organiser = organiser;
	}
	
	@Override
	public void run() {
		while(state != TERMINATING) {
			if(state == BUISY) {
				while(!queue.isEmpty()) {
					Request request = queue.poll();
					switch(request.identify()) {
					case Request.TERMINATE:
						terminate();
						break;
					case Request.RECIEVE_HEADER_REQUEST:
						recieveHeader(((RecieveHeaderRequest)request).getLine());
						break;
					case Request.RECIEVE_LINE_REQUEST:
						recieveLine(((RecieveLineRequest)request).getLine());
						break;
					}
				}
				if(state != TERMINATING)
					organiser.recieveRequest(new FinishedTaskRequest(index));
			}
			try {
				if((state == BUISY)&&(queue.isEmpty()))
					state = WAITING;
				sleep(10);
				System.out.println(getName() + ": state: " + state);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Adding next task to queue
	 * @param request
	 */
	public void recieve(Request request) {
		if(state != TERMINATING) {
			queue.add(request);
			if(request.identify() != Request.TERMINATE)
				organiser.recieveRequest(new Confirmation(index));
		}
	}
	
	private void recieveHeader(String header) {
		this.header = header.split(";");
	}
	
	private void recieveLine(String line) {
		String[] values = line.split(";");
		for(int i = 0; i < values.length; i++) {
			MemoryEntry entry = memory.getMemoryEntry();
			entry.set(header[i], values[i]);
			memory.write(entry);
		}
	}
	
	private void terminate() {
		state = TERMINATING;
	}
}
