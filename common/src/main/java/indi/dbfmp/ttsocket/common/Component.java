package indi.dbfmp.ttsocket.common;

/**
 * 组件接口，每个程序应由多个组件构成
 * @param <T> 父组件类型
 */
public interface Component<T extends Component<?>> extends Lifecycle,Named {

    /**
     * 设置组件名称
     *
     * @param name 组件名
     */
    void setName(String name);

    /**
     * @return 父组件
     */
    default T getParentComponent() {
        throw new UnsupportedOperationException(String.format("This component[%s] are not support to get parent component.", getName()));
    }



}
