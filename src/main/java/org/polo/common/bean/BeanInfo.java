package org.polo.common.bean;

import java.io.Serializable;

/**
 * @author zzs
 * @ClassName ParentInfoObject.java
 * @Description 父类采集信息
 * @createTime 2022年03月17日 10:25:00
 */
public class BeanInfo implements Serializable {

    /**
     * 会话id: 由线程id和uuid组成
     */
    public String sessionId;

    /**
     * 应用名称
     */
    public String appName;

    /**
     * 主机地址
     */
    public String host;

    /**
     * 采集数据类型
     */
    public String modelType;
}
