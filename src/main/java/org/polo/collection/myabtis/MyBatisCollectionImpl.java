package org.polo.collection.myabtis;

import com.alibaba.fastjson.annotation.JSONField;
import org.polo.AgentSession;
import org.polo.collection.Collection;
import org.polo.collection.myabtis.BoundSqlAdapter;
import org.polo.common.bean.SqlInfo;
import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author zzs
 * @ClassName MybatisCollectionImpl.java
 * @Description Mybatis采集实现类
 * @createTime 2022年03月17日 10:37:00
 */
public class MyBatisCollectionImpl  implements Collection {

    private final static String BASE_EXECUTOR = "org.apache.ibatis.executor.BaseExecutor";

    @JSONField
    @Override
    public void register(Instrumentation instrumentation) {
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                if (!BASE_EXECUTOR.replaceAll("\\.", "/").equals(className)) {
                    return null;
                }
                ClassPool classPool = new ClassPool();
                classPool.appendSystemPath();
                try {
                    CtClass ctClass = classPool.get(BASE_EXECUTOR);
                    CtMethod ctMethod = ctClass.getDeclaredMethods("query")[1];
                    ctMethod.addLocalVariable("info", classPool.get(SqlInfo.class.getName()));
                    ctMethod.insertBefore("info = org.polo.collection.myabtis.BoundSqlAdapter.before($args);");
                    ctMethod.insertAfter("org.polo.collection.myabtis.BoundSqlAdapter.end(info);");
                    return ctClass.toBytecode();
                } catch (NotFoundException | CannotCompileException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

}
