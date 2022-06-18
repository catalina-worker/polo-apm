package org.polo.collection.myabtis;

import org.polo.AgentSession;
import org.polo.common.bean.SqlInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zzs
 * @ClassName BoundSqlAdapter.java
 * @Description
 * @createTime 2022年03月18日 13:56:00
 */
public class BoundSqlAdapter {
    Object target;
    private static Method getSql;
    private static Class aClass;

    public static synchronized void init(Class cls) {
        aClass = cls;
        try {
            getSql = cls.getDeclaredMethod("getSql");
            getSql.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public BoundSqlAdapter(Object target) {
        this.target = target;
        if (aClass == null) {
            init(target.getClass());
        }
        this.target = target;
    }

    public String getSql() {
        try {
            return (String) getSql.invoke(target);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SqlInfo before(Object[] params) {
        AgentSession.open();
        SqlInfo sqlInfo = new SqlInfo();
        sqlInfo.beforeTime = System.currentTimeMillis();
        // 通过反射获取sql, 解决classLoad问题
        sqlInfo.sql = new BoundSqlAdapter(params[5]).getSql();
        return sqlInfo;
    }

    public static void end(SqlInfo sqlInfo) {
        sqlInfo.sqlTime = System.currentTimeMillis() - sqlInfo.beforeTime;
        AgentSession.get().put(sqlInfo);
    }
}
