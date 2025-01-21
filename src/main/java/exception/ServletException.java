package exception;

public class ServletException extends RuntimeException {

  public ServletException(String message) {
        super(message);
    }
  public ServletException(Exception e) {
    super(e);
  }
}
