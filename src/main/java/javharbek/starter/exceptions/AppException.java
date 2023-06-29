package javharbek.starter.exceptions;


public class AppException extends AbstractException {
    public String code;
    public String errorData;


    public AppException(String message) {
        super(message);
    }

    public AppException(String message, String code) {
        super(message);
        this.code = code;
    }
    public AppException(String message, String code,String errorData) {
        super(message);
        this.code = code;
        this.errorData = errorData;
    }

    @Override
    public String code() {
        if (code == null) {
            this.code = this.getClass().getSimpleName();
        }
        return this.code;
    }
}
