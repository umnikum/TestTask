package home.TestTask;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskExecutor extends Thread{
	
	private ConcurrentLinkedQueue<Command> queue;
	private String[] header;
	private MemoryStructure memory;
	private int state;
	public static final int BUISY = 1, TERMINATING = 0;
	
	
	public TaskExecutor() {
		queue = new ConcurrentLinkedQueue<Command>();
		state = BUISY;
	}
	
	public void connectToMemory(MemoryStructure memoryLink) {
		memory = memoryLink;
	}
	
	@Override
	public void run() {
		while(state != TERMINATING) {
			while(!queue.isEmpty()) {
				Command command = queue.poll();
				switch(command.identify()) {
				case Command.TERMINATE:
					terminate();
					break;
				case Command.RECIEVE_HEADER_COMMAND:
					recieveHeader(((RecieveHeaderCommand)command).getLine());
					break;
				case Command.RECIEVE_LINE_COMMAND:
					recieveLine(((RecieveLineCommand)command).getLine());
					break;
				}
			}
			try {
				sleep(50);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Adding next task to queue
	 * @param command
	 */
	public void execute(Command command) {
		if(state != TERMINATING)
			queue.add(command);
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
