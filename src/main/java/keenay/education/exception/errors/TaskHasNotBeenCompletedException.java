package keenay.education.exception.errors;

public class TaskHasNotBeenCompletedException extends RuntimeException {
    public TaskHasNotBeenCompletedException(String message) {
        super(message);
    }
}
