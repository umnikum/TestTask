package home.TestTask;


public interface Request {
	public static final int RECIEVE_HEADER_REQUEST = 1, RECIEVE_LINE_REQUEST = 2, TERMINATE = 0, FINISHED_TASK_REQUEST = 3, CONFIRMATION = 4;
	public int identify();
}
