package indi.dbfmp.ttsocket.common;

import java.util.EventListener;

/**
 * 生命周期事件监听器
 */
public interface LifecycleEventListener extends EventListener {

    /**
     * 生命周期事件
     * @param event
     */
    void lifecycleEvent(LifecycleEvent event);

}
