package home.TestTask;

public class Confirmation implements Request {

	public int executorIndex;
	
	public Confirmation(int index) {
		this.executorIndex = index;
	}
	
	public int identify() {
		return Request.CONFIRMATION;
	}

}
