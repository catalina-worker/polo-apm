package org.polo.collection.jdbc;

import org.polo.common.bean.SqlInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;

/**
 * @author ：zzs
 * @version : 1.0
 * @date ：Created in 2022/3/22 19:37
 * @description：
 */
public class ConnectionHandler implements InvocationHandler {

    private final Connection connection;

    public ConnectionHandler(Connection connection) {
        this.connection = connection;
    }

    private final static String[] STATEMENT_AGENT = {"prepareStatement"};

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        SqlInfo sqlInfo = new SqlInfo();
        boolean statement = Arrays.stream(STATEMENT_AGENT).allMatch(e -> e.equals(method.getName()));
        if (statement) {
            // 采集信息
            sqlInfo = JDBCAdapter.begin(connection, (String) args[0]);
        }
        result = method.invoke(connection, args);
        if (result instanceof PreparedStatement) {
            // 下个组件处理
            result = JDBCAdapter.proxyPreparedStatement((PreparedStatement) result, sqlInfo);
        }
        return result;
    }
}
