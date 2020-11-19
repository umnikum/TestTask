package home.TestTask;

public class MemoryEntry {
	
	public String header, value;
	public boolean utilised;
	
	public MemoryEntry() {
		utilised = false;
	}
	
	public void set(String header, String value) {
		this.header = header;
		this.value = value;
		utilised = true;
	}
}
