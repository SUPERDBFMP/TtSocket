package indi.dbfmp.ttsocket.protocol;
/**
 * 序列化时数据格式有误抛出的异常
 */
public class SerializationException extends Exception {

    private static final StackTraceElement[] NONE_STACK = new StackTraceElement[0];

    private final Class<? extends ScMessage> messageClass;

    public SerializationException(String msg) {
        super(msg);
        this.messageClass = null;
    }

    public SerializationException(String msg, Throwable t) {
        super(msg, t);
        this.messageClass = null;
    }

    public SerializationException(Throwable t) {
        super(t);
        this.messageClass = null;
    }

    public SerializationException(Class<? extends ScMessage> messageClass, String msg) {
        super(toMessage(messageClass, msg));
        this.messageClass = messageClass;
    }

    public SerializationException(Class<? extends ScMessage> messageClass, Throwable t) {
        super("[" + messageClass.getName() + "]", t);
        this.messageClass = messageClass;
    }

    public SerializationException(Class<? extends ScMessage> messageClass, String message, Throwable t) {
        super("[" + messageClass.getName() + "] " + message, t);
        this.messageClass = messageClass;
    }


    private static String toMessage(Class<? extends ScMessage> messageClass, String msg) {
        return "[" + messageClass.getName() + "]" + msg;
    }

    public Class<? extends ScMessage> getMessageClass() {
        return messageClass;
    }
    /**
     * 不爬栈
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        setStackTrace(NONE_STACK);
        return this;
    }
}
