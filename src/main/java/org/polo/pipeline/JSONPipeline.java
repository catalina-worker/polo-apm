package org.polo.pipeline;

import com.alibaba.fastjson.JSON;
import org.polo.AgentSession;

import java.util.Objects;


/**
 * @author zzs
 * @ClassName JSONPipeline.java
 * @Description json化处理
 * @createTime 2022年03月18日 14:40:00
 */
public class JSONPipeline implements Pipeline<Object, String> {

    @Override
    public int order() {
        return 2;
    }

    @Override
    public String accept(AgentSession agentSession, Object o) {
        // json化处理
        Objects.requireNonNull(o);
        // 本项目引入的jar在同一个classLoad中不存在访问双亲委派的问题所以不用反射代理
        // 如果是目标类的jar包需要反射代理
        return JSON.toJSONString(o);
    }
}
