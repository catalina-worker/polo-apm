package org.polo.common.utils;/**
 * Created by Administrator on 2018/6/4.
 */

import java.lang.reflect.Method;

/**
 * @author Tommy
 *         Created by Tommy on 2018/6/4
 **/
public class ReflectUtil {
    public static Object invoker(Method method, Object target, Object... args) {
        boolean old = method.isAccessible();
        try {
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            method.setAccessible(old);
        }
    }
}
