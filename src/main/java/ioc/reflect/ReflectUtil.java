package ioc.reflect;

/**
 * Created by chuang on 2017/8/21.
 */

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class ReflectUtil {

    // ------------------------------------------------------

    /** 新建对象 */
    public static Object newInstance(String className) {
        Object obj = null;
        try {
            Class<?> clazz = Class.forName(className);
            obj = clazz.newInstance();
        } catch (Exception e) {
            // quiet
        }
        return obj;
    }

    /**
     * 创建一个实例对象
     *
     * @param clazz class对象
     * @return
     */
    public static Object newInstance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 类型转换 */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object value, Class<T> type) {
        if (value != null && !type.isAssignableFrom(value.getClass())) {
            if (is(type, int.class, Integer.class)) {
                value = Integer.parseInt(String.valueOf(value));
            } else if (is(type, long.class, Long.class)) {
                value = Long.parseLong(String.valueOf(value));
            } else if (is(type, float.class, Float.class)) {
                value = Float.parseFloat(String.valueOf(value));
            } else if (is(type, double.class, Double.class)) {
                value = Double.parseDouble(String.valueOf(value));
            } else if (is(type, boolean.class, Boolean.class)) {
                value = Boolean.parseBoolean(String.valueOf(value));
            } else if (is(type, String.class)) {
                value = String.valueOf(value);
            }
        }
        return (T) value;
    }

    /** 查找方法 */
    public static Method getMethodByName(Object classOrBean, String methodName) {
        Method ret = null;
        if (classOrBean != null) {
            Class<?> clazz = null;
            if (classOrBean instanceof Class<?>) {
                clazz = (Class<?>) classOrBean;
            } else {
                clazz = classOrBean.getClass();
            }
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName)) {
                    ret = method;
                    break;
                }
            }
        }
        return ret;
    }

    public static Method getMethodByName(Class<?> clazz, String methodName) {
        Method ret = null;
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                ret = method;
                break;
            }
        }
        return ret;
    }

    // ------------------------------------------------------

    /** 对象是否其中一个 */
    public static boolean is(Object obj, Object... mybe) {
        if (obj != null && mybe != null) {
            for (Object mb : mybe)
                if (obj.equals(mb)) {
                    return true;
                }
        }
        return false;
    }

    public static boolean isNot(Object obj, Object... mybe) {
        return !is(obj, mybe);
    }

    // ------------------------------------------------------

    /** 扫描包下面所有的类 */
    public static List<String> scanPackageClass(String rootPackageName) {
        List<String> classNames = new ArrayList<String>();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL url = loader.getResource(rootPackageName.replace('.', '/'));

            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                File[] files = new File(url.toURI()).listFiles();
                for (File f : files) {
                    scanPackageClassInFile(rootPackageName, f, classNames);
                }
            } else if ("jar".equals(protocol)) {
                JarFile jar = ((JarURLConnection) url.openConnection())
                        .getJarFile();
                scanPackageClassInJar(jar, rootPackageName, classNames);
            }

        } catch (URISyntaxException e) {
        } catch (IOException e) {
        }
        return classNames;
    }

    /** 扫描文件夹下所有class文件 */
    private static void scanPackageClassInFile(String rootPackageName,
                                               File rootFile, List<String> classNames) {
        String absFileName = rootPackageName + "." + rootFile.getName();
        if (rootFile.isFile() && absFileName.endsWith(".class")) {
            classNames.add(absFileName.substring(0, absFileName.length() - 6));
        } else if (rootFile.isDirectory()) {
            String tmPackageName = rootPackageName + "." + rootFile.getName();
            for (File f : rootFile.listFiles()) {
                scanPackageClassInFile(tmPackageName, f, classNames);
            }
        }
    }

    /**
     * 扫描jar里面的类
     *
     * @param jar jar包文件
     * @param packageDirName 包目录
     * @param classNames class名称列表
     */
    private static void scanPackageClassInJar(JarFile jar, String packageDirName, List<String> classNames) {
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName().replace('/', '.');
            if (name.startsWith(packageDirName) && name.endsWith(".class")) {
                classNames.add(name.substring(0, name.length() - 6));
            }
        }
    }

}
