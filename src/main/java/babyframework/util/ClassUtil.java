package babyframework.util;

import babyframework.annotation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassUtil {
    private static Logger logger = LoggerFactory.getLogger(ClassUtil.class);
    private static Set<Class<?>> CLASS_SET = new HashSet<Class<?>>();

    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类
     * @param className
     * @param isInitialized 是否需要执行类的静态代码块
     * @return
     */
    public static Class<?> loadClass(String className,boolean isInitialized) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className,isInitialized,getClassLoader());
        } catch (ClassNotFoundException e) {
            logger.error("无法获取{}类",className);
            e.printStackTrace();
        }
        return clazz;
    }

    /**
     * 加载类，并执行类的静态代码块
     * @param className
     * @return
     */
    public static Class<?> loadClass(String className) {
        return loadClass(className,true);
    }

    /**
     * 获取controller注解的类的class
     * @return
     */
    public static Set<Class<?>> getControllerClass() {
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        for(Class cls : CLASS_SET) {
            if(cls.isAnnotationPresent(Controller.class)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * 获取指定包下的所有类
     */
    public static Set<Class<?>> getClassSet(String packageName) {

        try {
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".","/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if(url != null) {
                    String protocol = url.getProtocol();
                    if(protocol.equals("file")) {
                        String packagePath = url.getPath().replaceAll("%20"," ");
                        addClass(CLASS_SET,packagePath,packageName);
                    } else if(protocol.equals("jar")) {
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        JarFile jarFile = jarURLConnection.getJarFile();
                        if(jarFile != null) {
                            Enumeration<JarEntry> jarEntries = jarFile.entries();
                            while (jarEntries.hasMoreElements()) {
                                JarEntry jarEntry = jarEntries.nextElement();
                                String jarEntryName = jarEntry.getName();
                                if(jarEntryName.endsWith(".class")) {
                                    String className = jarEntryName.substring(0,jarEntryName.lastIndexOf(".")).replaceAll("/",".");
                                    doAddClass(CLASS_SET,className);
                                }
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            logger.error("无法获取{}下的类",packageName);
            e.printStackTrace();
        }
        return CLASS_SET;
    }

    private static void addClass(Set<Class<?>> classSet,String packagePath,String packageName) {
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });

        for(File file : files) {
            String fileName = file.getName();
            if(file.isFile()) {
                String className = fileName.substring(0,fileName.lastIndexOf("."));
                if(StringUtil.isNotEmpty(packageName)) {
                    className = packageName + "." + className;
                }
                doAddClass(classSet,className);
            } else {
                String subPackagePath = fileName;
                if(StringUtil.isNotEmpty(packagePath)) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }

                String subPackageName = fileName;
                if(StringUtil.isNotEmpty(packageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }

                addClass(classSet,subPackagePath,subPackageName);
            }
        }
    }

    private static void doAddClass(Set<Class<?>> classSet,String className) {
        Class<?> clazz = loadClass(className,false);
        classSet.add(clazz);
    }
}