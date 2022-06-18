package org.polo.pipeline;

import org.polo.Agent;
import org.polo.AgentSession;
import org.polo.common.bean.BeanInfo;
import org.polo.common.utils.NetUtils;

/**
 * @author ：zzs
 * @version : 1.0
 * @date ：Created in 2022/3/23 19:52
 * @description：跟踪会话id管道
 */
public class TraceSessionIDPipeline implements Pipeline<BeanInfo, BeanInfo>{

    @Override
    public int order() {
        return 1;
    }

    @Override
    public BeanInfo accept(AgentSession agentSession, BeanInfo parentInfo) {
        if (parentInfo != null) {
            parentInfo.sessionId = agentSession.getSessionId();
            parentInfo.appName = Agent.config.getProperty("app.name", "未定义！");
            parentInfo.host = NetUtils.getLocalHost();
            parentInfo.modelType = parentInfo.getClass().getSimpleName();
        }
        return parentInfo;
    }
}
