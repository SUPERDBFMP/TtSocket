package indi.dbfmp.ttsocket.common;

import indi.dbfmp.ttsocket.common.listener.LifecycleLoggerListener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;

@Slf4j
public class AbstractComponent<T extends Component<?>> extends LifecycleBase implements Component<T> {

    /**
     * 父组件引用
     */
    protected final T parent;

    /**
     * 该组件的名称
     */
    protected String name;

    /**
     * 该组件持有的子组件，键为该组件的名称
     */
    private final Map<String, Component<?>> componentMap = new ConcurrentSkipListMap<>();


    protected AbstractComponent() {
        super(LifecycleLoggerListener.INSTANCE);
        this.parent = null;
        this.name = getClass().getName();
    }

    /**
     * 构造组件
     *
     * @param name   组件名
     * @param parent 父组件，如果没有父组件则泛型参数T需为VoidComponent并且值为null
     */
    protected AbstractComponent(String name, T parent) {
        super(LifecycleLoggerListener.INSTANCE);
        this.name = Objects.requireNonNull(name);
        this.parent = parent;
    }

    protected AbstractComponent(String name) {
        this(name, null);
    }

    @Override
    public void setName(String name) {
        if (parent instanceof AbstractComponent<?>) {
            AbstractComponent<?> c = (AbstractComponent<?>) parent;
            c.changeChildComponentName(this.name, name);
        }
        this.name = name;
    }

    /**
     * 当子组件需要修改名称时由子组件调用
     *
     * @param oldName 子组件旧名称
     * @param newName 子组件新名称
     */
    private void changeChildComponentName(String oldName, String newName) {
        Component<?> c = componentMap.get(oldName);
        if (c == null) {
            throw new ComponentException("Can not find component: " + oldName + " at " + name);
        }
        if (componentMap.containsKey(newName)) {
            throw new ComponentException("Component name: " + newName + " is already in " + name);
        }

        componentMap.remove(oldName);
        componentMap.put(newName, c);
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public T getParentComponent() {
        return parent;
    }

    /**
     * 添加子组件
     *
     * @param component 组件对象
     */
    protected synchronized void addComponent(Component<?> component) {
        String name = component.getName();
        if (name == null) {
            throw new ComponentException(String.format("Component type [%s] name can not be null.", component.getClass().getName()));
        }
        if (componentMap.get(name) != null) {
            throw new ComponentException(String.format("Component [%s] already has child component [%s].", getName(), name));
        }

        componentMap.put(name, component);
    }

    /**
     * 根据组件名称获取组件
     *
     * @param name 组件名
     * @return 组件，若没有找到返回null
     */
    protected Component<?> getComponentByName(String name) {
        return componentMap.get(name);
    }

    @SuppressWarnings("unchecked")
    protected <V extends Component<?>> V getComponentByName(String name, Class<V> requireType) {
        Component<?> c = componentMap.get(name);
        if (c == null) {
            return null;
        }

        if (!requireType.isInstance(c)) {
            throw new ComponentException(new ClassCastException(String.format("Component [%s] is not type of %s.", getName(), requireType.getName())));
        }

        return (V) c;
    }

    /**
     * 移除组件
     *
     * @param name 组件名
     */
    protected void removeComponentByName(String name) {
        componentMap.remove(name);
    }

    @Override
    protected void initInternal() {
        for (Map.Entry<String, Component<?>> entry : componentMap.entrySet()) {
            try {
                entry.getValue().init();
            } catch (Exception e) {
                log.error(String.format("Component [%s] init failure.", entry.getKey()), e);
                handleException(entry.getValue(), e);
            }
        }
    }

    protected void handleException(Component<?> component, Exception exception) {
    }

    @Override
    protected void startInternal() {
        for (Map.Entry<String, Component<?>> entry : componentMap.entrySet()) {
            try {
                entry.getValue().start();
            } catch (Exception e) {
                log.error(String.format("Component [%s] start failure.", entry.getKey()), e);
                handleException(entry.getValue(), e);
            }
        }
    }

    @Override
    protected void stopInternal() {
            for (Map.Entry<String, Component<?>> entry : componentMap.entrySet()) {
                try {
                    entry.getValue().stop();
                } catch (Exception e) {
                    log.error(String.format("Component [%s] stop failure.", entry.getKey()), e);
                    handleException(entry.getValue(), e);
                }
            }

    }

    @Override
    protected void restartInternal() {
            for (Map.Entry<String, Component<?>> entry : componentMap.entrySet()) {
                try {
                    entry.getValue().restart();
                } catch (Exception e) {
                    log.error(String.format("Component [%s] restart failure.", entry.getKey()), e);
                    handleException(entry.getValue(), e);
                }
            }

        super.restartInternal();
    }

}
