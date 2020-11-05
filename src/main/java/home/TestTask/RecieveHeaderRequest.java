package home.TestTask;

public class RecieveHeaderRequest implements RecieveRequest {

	private String header;
	
	public RecieveHeaderRequest(String string) {
		header = string;
	}
	
	public int identify() {
		return RECIEVE_HEADER_REQUEST;
	}

	public String getLine() {
		return header;
	}
}
