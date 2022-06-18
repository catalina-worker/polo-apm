package org.polo.collection.jdbc;

import org.polo.common.bean.SqlInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;

/**
 * @author ：zzs
 * @version : 1.0
 * @date ：Created in 2022/3/22 19:44
 * @description：
 */
public class PreparedStatementHandler implements InvocationHandler {

    private final PreparedStatement preparedStatement;

    private final SqlInfo sqlInfo;

    private final String[] EXECUTE = {"executeQuery", "executeUpdate", "execute"};

    public PreparedStatementHandler(PreparedStatement preparedStatement, SqlInfo sqlInfo) {
        this.preparedStatement = preparedStatement;
        this.sqlInfo = sqlInfo;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean execute = false;
        for (String ex : EXECUTE) {
            if (ex.equals(method.getName())) {
                execute = true;
                break;
            }
        }
        Object result = null;
        try {
            result = method.invoke(preparedStatement, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            if (execute) {
                JDBCAdapter.end(sqlInfo, e);
            }
        } finally {
            if (execute) {
                JDBCAdapter.end(sqlInfo, null);
            }
        }
        return result;
    }
}
