package org.polo.pipeline;


import org.polo.AgentSession;

/**
 * @author zzs
 */
public interface Pipeline<T, R> {

    /**
     * 优先级顺序
     * @return
     */
    default int order() {
        return 0;
    }

    R accept(AgentSession agentSession, T t);

    default void finish() {
        // 关闭会话时 执行
    }
}
