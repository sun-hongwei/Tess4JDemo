package per.zh.tess4j.RSA;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义 ClassLoader
 */
public class ByteLoader extends ClassLoader {

    private Map<String, byte[]> itemBytes = new HashMap<>();

    public ByteLoader() {
        super(ByteLoader.class.getClassLoader());
    }

    public void addByte(String name, byte[] bytes) {
        itemBytes.put(name, bytes);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clazz = this.findLoadedClass(name);
        if (clazz == null) {
            byte[] bytes = itemBytes.get(name);
            if (bytes == null) {
                throw new ClassNotFoundException("class not found : " + name);
            }
            clazz = this.defineClass(name, bytes, 0, bytes.length);
        }
        return clazz;
    }
}
