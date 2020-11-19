package home.TestTask;

public class ProcessState {
	public enum StateType{
		IDDLE,
		BUSY,
		TERMINATING;
	}
	
	private StateType state;
	
	public ProcessState() {
		state = StateType.IDDLE;
	}
	
	public StateType getState() {
		return state;
	}
	
	synchronized public void setState(StateType newState) {
		switch(state) {
		case IDDLE:
			switch(newState) {
			case BUSY:
				state = StateType.BUSY;
				break;
			case TERMINATING:
				state = StateType.TERMINATING;
			default:
			}
			break;
		case BUSY:
			switch(newState) {
			case IDDLE:
				state = StateType.IDDLE;
				break;
			case TERMINATING:
				state = StateType.TERMINATING;
			default:
			}
			break;
		case TERMINATING:
			default:
		}
	}
	
}
