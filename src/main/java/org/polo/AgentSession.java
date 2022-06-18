package org.polo;

import org.junit.platform.commons.util.ClassFilter;
import org.junit.platform.commons.util.ReflectionUtils;
import org.polo.pipeline.Pipeline;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author zzs
 * @ClassName ThreadLocalUtil.java
 * @Description
 * @createTime 2022年03月18日 14:19:00
 */
public class AgentSession {

    public static final ThreadLocal<AgentSession> LOCAL = new ThreadLocal<>();

    private static final List<Class<?>> PIPELINES;

    private List<Pipeline> pipelines;

    private String sessionId;


    static {
        // 获取所有管道实现类
        PIPELINES = ReflectionUtils.findAllClassesInPackage(AgentSession.class.getPackage().getName(),
                ClassFilter.of(o -> Pipeline.class.isAssignableFrom(o) && !o.isInterface()));
    }

    public AgentSession() {
        // 如果会话id前缀是一致那么就是同一请求
        this.sessionId = Thread.currentThread().getId() + "|" + UUID.randomUUID().toString().replaceAll("-", "").substring(1, 9);
        pipelines = PIPELINES.stream().map(item -> {
            try {
                return (Pipeline) item.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }).sorted(Comparator.comparingInt(Pipeline::order)).collect(Collectors.toList());
    }

    public static AgentSession get() {
        return LOCAL.get();
    }

    public static void close() {
        // 完成所有处理器
        try {
            if (get() != null) {
                get().pipelines.forEach(Pipeline::finish);
            }
        } finally {
            LOCAL.remove();
        }
    }

    public static void open() {
        AgentSession agentSession = new AgentSession();
        LOCAL.set(agentSession);
    }

    public void put(Object node) {
        Objects.requireNonNull(node);
        for (Pipeline pipeline : pipelines) {
            node = pipeline.accept(this, node);
            if (node == null) {
                break;
            }
        }
    }

    public String getSessionId() {
        return sessionId;
    }
}
