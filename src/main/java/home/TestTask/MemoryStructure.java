package home.TestTask;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MemoryStructure extends Thread{
	private ConcurrentLinkedQueue<MemoryEntry> queue;
	private Hashtable<String, List<String>> memory;
	private MemoryEntryPool pool;
	
	private int state;
	public static final int BUSY = 1, TERMINATING = 0;
	
	public MemoryStructure() {
		setName("Memory");
		pool = new MemoryEntryPool();
		memory = new Hashtable<String, List<String>>();
		queue = new ConcurrentLinkedQueue<MemoryEntry>();
		state = BUSY;
	}
	
	public void terminate() {
		state = TERMINATING;
	}
	
	public void run() {
		while((state != TERMINATING)||(!queue.isEmpty())) {
			while(!queue.isEmpty()){
				MemoryEntry entry = queue.poll();
				addValue(entry);
				entry.utilised = false;
			}
			try {
				sleep(10);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.print(toString());
	}
	
	public MemoryEntry getMemoryEntry() {
		return pool.getMemoryEntry();
	}
	
	public void write(MemoryEntry entry) {
		queue.add(entry);
	}
	
	private void addValue(MemoryEntry entry) {
		if(!wasHeaderWritten(entry.header))
			memory.put(entry.header, new ArrayList<String>());
		if((entry.value != null)&&(!wasValueWritten(entry.header, entry.value))) {
			memory.get(entry.header).add(new String(entry.value));
		}
	}

	private boolean wasHeaderWritten(String header) {
		return memory.containsKey(header);
	}
	
	//speed optimization: sorted list + binary search function
	private boolean wasValueWritten(String header, String value) {
		return memory.get(header).contains(value);
	}
	
	//possibly string buffer needed
	@Override
	public String toString() {
		String answer = "";
		Enumeration<String> keys = memory.keys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			answer += key + ":\n";
			List<String> values = memory.get(key);
			for(String value:values)
				answer += value + ";";
			answer += "\n";
		}
		return answer;
	}
}
