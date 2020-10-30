package home.TestTask;

public class RecieveHeaderCommand implements RecieveCommand {

	private String header;
	
	public RecieveHeaderCommand(String string) {
		header = string;
	}
	
	public int identify() {
		return RECIEVE_HEADER_COMMAND;
	}

	public String getLine() {
		return header;
	}
}
