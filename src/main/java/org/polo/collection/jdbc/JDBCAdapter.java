package org.polo.collection.jdbc;

import org.polo.AgentSession;
import org.polo.common.bean.SqlInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * @author ：zzs
 * @version : 1.0
 * @date ：Created in 2022/3/22 19:33
 * @description：JDBC适配器
 */
public class JDBCAdapter {

    public static Connection proxyConnection(final Connection connection) {
        return (Connection) Proxy.newProxyInstance(JDBCAdapter.class.getClassLoader(),
                new Class[]{Connection.class},
                new ConnectionHandler(connection));
    }

    public static PreparedStatement proxyPreparedStatement(final PreparedStatement preparedStatement, SqlInfo sqlInfo) {
        return (PreparedStatement) Proxy.newProxyInstance(JDBCAdapter.class.getClassLoader(),
                new Class[]{PreparedStatement.class},
                new PreparedStatementHandler(preparedStatement, sqlInfo));
    }


    public static SqlInfo begin(Connection connection, String sql) throws SQLException {
        AgentSession.open();
        SqlInfo sqlInfo = new SqlInfo();
        sqlInfo.createdTime = LocalDateTime.now();
        sqlInfo.sql = sql;
        sqlInfo.beforeTime = System.currentTimeMillis();
        sqlInfo.dataBaseName = getDbName(connection.getMetaData().getURL());
        return sqlInfo;
    }

    public static void end(SqlInfo sqlInfo, Throwable throwable) {
        if (sqlInfo != null) {
            sqlInfo.sqlTime = System.currentTimeMillis() - sqlInfo.beforeTime;
            if (throwable != null) {
                if (throwable instanceof InvocationTargetException) {
                    sqlInfo.error = ((InvocationTargetException) throwable).getTargetException().getMessage();
                } else {
                    sqlInfo.error = throwable.getMessage();
                }
            }
            // 处理器处理数据
            AgentSession.get().put(sqlInfo);
            AgentSession.close();
        }
    }

    static String getDbName(String url) {
        int index = url.indexOf("?"); //$NON-NLS-1$
        if (index != -1) {
            String paramString = url.substring(index + 1, url.length());
            url = url.substring(0, index);
        }
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
