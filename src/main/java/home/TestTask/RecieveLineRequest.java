package home.TestTask;

public class RecieveLineRequest implements RecieveRequest {

	private String line;
	
	public RecieveLineRequest(String string) {
		line = string;
	}
	
	public int identify() {
		return RECIEVE_LINE_REQUEST;
	}

	public String getLine() {
		return line;
	}

}
