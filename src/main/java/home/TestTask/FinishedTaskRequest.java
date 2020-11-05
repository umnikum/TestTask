package home.TestTask;

public class FinishedTaskRequest implements Request {

	public int executorIndex;
	
	public FinishedTaskRequest(int index) {
		executorIndex = index;
	}
	
	public int identify() {
		return Request.FINISHED_TASK_REQUEST;
	}

}
