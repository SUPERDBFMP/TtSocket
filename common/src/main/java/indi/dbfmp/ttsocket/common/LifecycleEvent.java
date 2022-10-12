package indi.dbfmp.ttsocket.common;

import java.util.EventObject;

/**
 * 当一个具有生命周期对象的状态发生变动时的事件
 *
 */
public class LifecycleEvent extends EventObject {

    private static final long serialVersionUID = 1688342819534381597L;

    private final String type;

    private final Object data;

    public LifecycleEvent(Lifecycle source, String type) {
        this(source, type, null);
    }

    public LifecycleEvent(Lifecycle source, String type, Object data) {
        super(source);
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
