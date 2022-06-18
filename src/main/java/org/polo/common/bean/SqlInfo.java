package org.polo.common.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zzs
 * @ClassName SqlInfo.java
 * @Description sql信息类
 * @createTime 2022年03月17日 10:25:00
 */
public class SqlInfo extends BeanInfo implements Serializable {

    public String sql;

    public Long beforeTime;

    public Long sqlTime;

    public LocalDateTime createdTime;

    /**
     * 异常信息
     */
    public String error;

    /**
     * 数据库类型
     */
    public String dataBaseName;

    @Override
    public String toString() {
        return "SqlInfo{" +
                "sql='" + sql + '\'' +
                ", beforeTime=" + beforeTime +
                ", sqlTime=" + sqlTime +
                ", error='" + error + '\'' +
                ", dataBaseName='" + dataBaseName + '\'' +
                '}';
    }
}
