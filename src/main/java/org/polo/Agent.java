package org.polo;

import org.junit.platform.commons.util.ClassFilter;
import org.junit.platform.commons.util.ReflectionUtils;
import org.polo.collection.Collection;
import org.polo.common.logger.Log;
import org.polo.common.logger.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * @author zzs
 * @ClassName org.polo.Agent.java
 * @Description
 * @createTime 2022年03月17日 10:23:00
 */
public class Agent {

    private final static Log logger = LogFactory.getLog(Agent.class);

    public static Properties config;

    public static void premain(String arg, Instrumentation instrumentation) {
        config = new Properties();
        // 装截agent 配置文件
        config.putAll(getAgentConfigs());
        // 基于JVM参数配置，优先级高
        if (arg != null && !arg.trim().equals("")) {
            try {
                config.load(new ByteArrayInputStream(
                        arg.replaceAll(",", "\n").getBytes()));
            } catch (IOException e) {
                throw new RuntimeException(String.format("agent参数无法解析：%s", arg), e);
            }
        }
        System.out.println("premain启动");
        // 扫描组件
        List<Class<?>> collections = ReflectionUtils.findAllClassesInPackage(Agent.class.getPackage().getName(),
                ClassFilter.of(o -> Collection.class.isAssignableFrom(o) && !o.isInterface()));
        for (Class<?> c : collections) {
            try {
                Collection collection = (Collection) c.newInstance();
                collection.register(instrumentation);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    // 读取agent 配置
    private static Properties getAgentConfigs() {
        // 读取agnet 配置
        URL u = Agent.class.getProtectionDomain().getCodeSource().getLocation();
        File file = new File(new File(u.getFile()), "conf/config.properties");
        if (!file.exists() || file.isDirectory()) {
            logger.warn("找不到配置文件:" + file.getPath());
            return new Properties();
        }
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }

    public static String getConfig(String s) {

        return config.getProperty(s);
    }

//    public static void main(String[] args) {
//        System.out.println(ReflectionUtils.findAllClassesInPackage(Agent.class.getPackage().getName(),
//                ClassFilter.of(o -> Collection.class.isAssignableFrom(o) && !o.isInterface())));
//    }
}
