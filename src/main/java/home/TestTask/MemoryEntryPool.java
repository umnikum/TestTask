package home.TestTask;

public class MemoryEntryPool {
	private MemoryEntry[] pool;
	private int index;
	private final int maxSize = 200;
	
	public MemoryEntryPool() {
		pool = new MemoryEntry[maxSize];
		index = 1;
		pool[index - 1] = new MemoryEntry();
	}
	
	synchronized public MemoryEntry getMemoryEntry() {
		MemoryEntry freeMemory = null;
		while(freeMemory == null) {
			for(int i = index - 1; i >= 0; i--)
				if(!pool[i].utilised) {
					freeMemory = pool[i];
					break;
				}
			if(index < maxSize) {
				index++;
				pool[index - 1] = new MemoryEntry();
				freeMemory = pool[index - 1];
			}
		}
		return freeMemory;
	}
}
