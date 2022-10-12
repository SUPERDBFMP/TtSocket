package indi.dbfmp.ttsocket.common;


import java.util.Objects;

/**
 * 配置模板类
 *
 */
public class AbstractConfig implements Config{
    /**
     * 配置名称
     */
    private final String name;

    /**
     * 隶属的配置管理器
     */
    protected final ConfigManager configManager;

    /**
     * 是否已经初始化过
     */
    private boolean initial = false;


    protected AbstractConfig(ConfigManager configManager, String name) {
        this.configManager = Objects.requireNonNull(configManager);
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public ConfigManager configManager() {
        return configManager;
    }

    @Override
    public synchronized final void initialize() throws ConfigInitializationException {
        if (initial) {
            throw new IllegalStateException("不能重复初始化配置 config " + getName());
        }

        try {
            initInternal();
            initial = true;
        } catch (ConfigInitializationException e) {
            throw e;
        }
    }

    protected void initInternal() throws ConfigInitializationException { }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;

        if (obj.getClass() == getClass()) {
            AbstractConfig c = (AbstractConfig) obj;
            return c.configManager.equals(this.configManager) && c.name.equals(this.name);
        }

        return false;
    }
}
