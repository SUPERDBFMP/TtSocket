package indi.dbfmp.ttsocket.common.util;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class ClasspathURLHandler extends URLStreamHandler {

    private final ClassLoader classLoader;

    ClasspathURLHandler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    protected URLConnection openConnection(URL u) {
        return new ClasspathURLConnection(u, classLoader);
    }

}
