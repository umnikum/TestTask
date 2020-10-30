package home.TestTask;

public class MemoryEntryPool {
	private MemoryEntry[] pool;
	private int index;
	
	public MemoryEntryPool() {
		pool = new MemoryEntry[200];
		index = 0;
		pool[index] = new MemoryEntry();
	}
	
	synchronized public MemoryEntry getMemoryEntry() {
		MemoryEntry freeMemory = null;
		while(freeMemory == null) {
			for(int i = index; i >= 0; i--)
				if(!pool[i].utilised)
					freeMemory = pool[i];
			if(index < 999) {
				index++;
				pool[index] = new MemoryEntry();
				freeMemory = pool[index];
			}
		}
		return freeMemory;
	}
}
