package org.polo.collection.jdbc;

import javassist.*;
import org.polo.collection.Collection;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author ：zzs
 * @version : 1.0
 * @date ：Created in 2022/3/22 19:07
 * @description：JDBC采集器
 */
public class JDBCCollectionImpl implements Collection {

    private static final String TARGET_CLASS = "com.mysql.cj.jdbc.NonRegisteringDriver";
    private static final String METHOD = "connect";

    @Override
    public void register(Instrumentation instrumentation) {
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className,
                                    Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                                    byte[] classfileBuffer) throws IllegalClassFormatException {
                if (!TARGET_CLASS.replaceAll("\\.", "/").equals(className)) {
                    return null;
                }
                ClassPool pool = new ClassPool();
                pool.appendSystemPath();
                try {
                    CtClass ctClass = pool.get(TARGET_CLASS);
                    CtMethod oldMethod = ctClass.getDeclaredMethods(METHOD)[0];
                    CtMethod newMethod = CtNewMethod.copy(oldMethod, ctClass, null);
                    oldMethod.setName(oldMethod.getName() + "$agent");
                    // 修改字节码。
                    // 在目标方法执行之前，让它调用JDBC适配器进行处理。
                    String endSrc = "result = org.polo.collection.jdbc.JDBCAdapter.proxyConnection((java.sql.Connection) result);";
                    newMethod.setBody(String.format(source, "", METHOD, "", endSrc));
                    ctClass.addMethod(newMethod);
                    return ctClass.toBytecode();
                } catch (NotFoundException | CannotCompileException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
    final static String source = "{\n"
            + "%s"
            + "        Object result=null;\n"
            + "       try {\n"
            + "            result=($w)%s$agent($$);\n"
            + "        } catch (Throwable e) {\n"
            + "%s"
            + "            throw e;\n"
            + "        }finally{\n"
            + "%s"
            + "        }\n"
            + "        return ($r) result;\n"
            + "}\n";
}
