package indi.dbfmp.ttsocket.common;


/**
 * 配置初始化时初始化错误所抛出的异常
 *
 */
public class ConfigInitializationException extends RuntimeException{

    public ConfigInitializationException() {
    }

    public ConfigInitializationException(String msg) {
        super(msg);
    }

    public ConfigInitializationException(String msg, Throwable t) {
        super(msg, t);
    }

    public ConfigInitializationException(Throwable t) {
        super(t);
    }


}
