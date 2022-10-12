package indi.dbfmp.ttsocket.common.util;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class ClasspathURLHandlerFactory implements URLStreamHandlerFactory {
    private static final String PROTOCOL = "classpath";

    static {
        ClassLoader cl = ClasspathURLHandlerFactory.class.getClassLoader();
        URL.setURLStreamHandlerFactory(new ClasspathURLHandlerFactory(cl));
    }

    private final ClassLoader classLoader;

    private ClasspathURLHandlerFactory(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (protocol.toLowerCase().equals(PROTOCOL)) {
            return new ClasspathURLHandler(classLoader);
        } else {
            return null;
        }
    }
}
