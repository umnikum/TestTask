package home.TestTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadFileTask implements Task {

	private MemoryStructure memory;
	private BufferedReader reader;
	
	public ReadFileTask(File file) throws FileNotFoundException {
		reader = new BufferedReader(new FileReader(file));
	}
	
	public void run(){
		try {
			String[] header = reader.readLine().split(";");
			String line = reader.readLine();
			while(line != null) {
				String[] values = line.split(";");
				for(int i = 0; i < values.length; i++) {
					MemoryEntry entry = memory.getMemoryEntry();
					entry.set(header[i], values[i]);
					memory.write(entry);
				}
				line = reader.readLine();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void connectToMemory(MemoryStructure memory) {
		this.memory = memory;
	}
}
