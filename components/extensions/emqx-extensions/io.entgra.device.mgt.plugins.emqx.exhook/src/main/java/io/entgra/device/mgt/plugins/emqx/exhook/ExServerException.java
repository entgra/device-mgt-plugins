package io.entgra.device.mgt.plugins.emqx.exhook;

public class ExServerException extends Exception{

    private static final long serialVersionUID = -2310817253324129270L;
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ExServerException(String msg, Exception nestedEx) {
        super(msg, nestedEx);
        setErrorMessage(msg);
    }

    public ExServerException(String message, Throwable cause) {
        super(message, cause);
        setErrorMessage(message);
    }

    public ExServerException(String msg) {
        super(msg);
        setErrorMessage(msg);
    }

    public ExServerException() {
        super();
    }

    public ExServerException(Throwable cause) {
        super(cause);
    }
}
