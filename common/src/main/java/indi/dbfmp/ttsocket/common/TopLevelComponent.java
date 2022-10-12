package indi.dbfmp.ttsocket.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class TopLevelComponent extends AbstractComponent<VoidComponent>{

    static {
        try {
            //加载这个类以便支持‘classpath:’类型的URL
            Class.forName("indi.dbfmp.ttsocket.common.util.ClasspathURLHandlerFactory");
        } catch (ClassNotFoundException e) {
            throw new ComponentException(e);
        }
    }


    protected final String version;

    protected TopLevelComponent() {
        super();
        this.version = readVersion();

    }

    protected TopLevelComponent(String name) {
        super(name, null);
        this.version = readVersion();
    }
    private static String readVersion() {
        try (InputStream versionInputStream = new URL("classpath://META-INF/version").openStream();
             Scanner sc = new Scanner(versionInputStream)) {
            String version = sc.nextLine();
            String tag = sc.nextLine();
            return version + "-" + tag;
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
