package babyframework.helper;

import babyframework.util.ClassUtil;

public final class LoaderHelper {
    public static void init() {
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                IocHelper.class,
                ControllerHelper.class
        };
        for(Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName());
        }
    }
}
