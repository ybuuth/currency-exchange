package exception;

public class DatabaseException extends RuntimeException {
    public DatabaseException(Throwable e) {
        super(e);
    }
    public DatabaseException(String e) {
        super(e);
    }
}
