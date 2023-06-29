package javharbek.starter.exceptions;


abstract public class AbstractException extends Exception{
    public AbstractException(String message) {
        super(message);
    }

    abstract public String code();
}
