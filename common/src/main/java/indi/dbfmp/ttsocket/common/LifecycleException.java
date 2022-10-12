package indi.dbfmp.ttsocket.common;

/**
 * 生命周期异常
 */
public class LifecycleException extends RuntimeException{

    private static final long serialVersionUID = 24045117437459820L;

    public LifecycleException() {
        super();
    }

    public LifecycleException(String message) {
        super(message);
    }

    public LifecycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public LifecycleException(Throwable cause) {
        super(cause);
    }
}
