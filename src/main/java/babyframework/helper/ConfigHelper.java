package babyframework.helper;

import babyframework.config.ConfigConstant;
import babyframework.util.PropsUtil;

import java.util.Properties;

public final class ConfigHelper {
    private static final Properties CONFIG_PROPS = PropsUtil.loadProps(ConfigConstant.CONFIG_FILE);

    /**
     * 获取JDBC驱动
     */
    public static String getJDBCDriver() {
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.JDBC_DRIVER,"com.mysql.jdbc.Driver");
    }

    /**
     * 获取JDBC URL
     */
    public static String getJDBCURL() {
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.JDBC_URL,"jdbc:mysql://localhost:3306/babyframework");
    }

    /**
     * 获取数据库连接用户名
     */
    public static String getConnectionUsername() {
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.DATABASE_USERNAME,"root");
    }

    /**
     * 获取数据库连接密码
     */
    public static String getConnectionPassword() {
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.DATABASE_PASSWORD,"1225");
    }

    /**
     * 获取应用基础包名
     */
    public static String getAppBasePackage() {
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.APP_BASE_PACKAGE,"babyframework");
    }

    /**
     * 获取JSP路径
     */
    public static String getJSPPath() {
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.APP_JSP_PATH,"/WEB-INF/view/");
    }

    /**
     * 获取JSP后缀
     */
    public static String getJSPENDWITH() {
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.APP_JSP_END_WITH,".jsp");
    }

    /**
     *获取静态资源路径
     */
    public static String getStaticPath() {
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.APP_STATIC_RESOURCE_PATH,"/static/");
    }


}
