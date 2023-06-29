package javharbek.starter.exceptions;

public class LoginException extends RuntimeException {

    private final Object data;

    public LoginException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return this.data;
    }
}
