package indi.dbfmp.ttsocket.common;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class DefaultConfigManager implements ConfigManager {

    public static final String NAME = "ConfigManager";

    /**
     * 配置存储Map
     */
    private final Map<String, Config> configMap = new ConcurrentHashMap<>();

    /**
     * 配置事件监听器集合
     */
    private final List<ConfigEventListener> listeners = new CopyOnWriteArrayList<>();


    @Override
    public void registerConfig(Config config) throws ConfigInitializationException {
        String name = config.getName();
        if (configMap.containsKey(name)) {
            throw new IllegalStateException(String.format("config %s 已经存在", name));
        }
        configMap.put(name, config);
        try {
            config.initialize();
        } catch (ConfigInitializationException e) {
            throw e;
        } catch (Exception e) {
            configMap.remove(name);
            throw new ComponentException(String.format("register config %s occur a exception", name), new ConfigInitializationException(e));
        }
        fireConfigEvent(Config.REGISTER_EVENT, config);
    }

    protected void fireConfigEvent(ConfigEvent configEvent) {
        for (ConfigEventListener listener : listeners)
            listener.configEvent(configEvent);
    }

    private void fireConfigEvent(String type, Config config) {
        ConfigEvent event = new ConfigEvent(type, config, this);
        fireConfigEvent(event);
    }

    @Override
    public void updateConfig(Config config) {
        String name = config.getName();
        if (!configMap.containsKey(name)) {
            throw new IllegalStateException(String.format("config %s 不存在", name));
        }
        configMap.put(name, config);
        fireConfigEvent(Config.UPDATE_EVENT, config);
    }

    @Override
    public void removeConfig(Config config) {
        String name = config.getName();
        if (!configMap.containsKey(name)) {
            throw new IllegalStateException(String.format("config %s 不存在", name));
        }
        configMap.remove(name);
        fireConfigEvent(Config.REMOVE_EVENT, config);
    }

    @Override
    public void removeConfig(String name) {
        if (!configMap.containsKey(name)) {
            throw new IllegalStateException(String.format("config %s 不存在", name));
        }
        Config remove = configMap.remove(name);
        fireConfigEvent(Config.REMOVE_EVENT, remove);
    }

    @Override
    public boolean saveConfig(Config config) {
        if (config.canSave()) {
            try {
                config.save();
                return true;
            } catch (Exception e) {
                log.warn("Save config '{}' occur a exception", config.getName(), e);
                return false;
            }
        }

        return false;
    }

    @Override
    public void saveAllConfig() {
        synchronized (configMap) {
            for (Config c : configMap.values()) {
                try {
                    if (c.canSave())
                        c.save();
                } catch (Exception e) {
                    log.warn("Save config '{}' occur a exception", c.getName(), e);
                }
            }
        }
    }

    @Override
    public Config getConfig(String name) {
        return configMap.get(name);
    }

    @Override
    public <C extends Config> C getConfig(String name, Class<C> requireType) {
        return (C) configMap.get(name);
    }

    @Override
    public void registerConfigEventListener(ConfigEventListener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    @Override
    public void removeConfigEventListener(ConfigEventListener listener) {
        listeners.remove(Objects.requireNonNull(listener));
    }

    @Override
    public String getEnvironmentVariable(String key) {
        return System.getenv(key);
    }

    @Override
    public String getSystemProperties(String key) {
        return System.getProperty(key);
    }

    @Override
    public void setSystemProperties(String key, String value) {
        System.setProperty(key, value);
    }

    @Override
    public InputStream loadResource(String path) throws IOException {
        try {
            URL url = new URL(path);
            return url.openStream();
        } catch (MalformedURLException e) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                return new FileInputStream(file);
            } else {
                if (log.isWarnEnabled())
                    log.warn("URL or file path error", e);
                return null;
            }
        } catch (IOException e) {
            if (log.isErrorEnabled())
                log.error("load resource {} occur a IOException", path);
            throw e;
        }
    }

    @Override
    public String getName() {
        return null;
    }
}
