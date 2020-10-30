package home.TestTask;

public class RecieveLineCommand implements RecieveCommand {

	private String line;
	
	public RecieveLineCommand(String string) {
		line = string;
	}
	
	public int identify() {
		return RECIEVE_LINE_COMMAND;
	}

	public String getLine() {
		return line;
	}

}
