package org.polo.pipeline;

import com.alibaba.fastjson.JSON;
import org.polo.Agent;
import org.polo.AgentSession;
import org.polo.common.logger.Log;
import org.polo.common.logger.LogFactory;
import org.polo.common.utils.Assert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * @author zzs
 * @ClassName PrintPipeline2.java
 * @Description 采集日志打印管道
 * @createTime 2022年03月18日 14:51:00
 */
public class LogPipeline implements Pipeline<String, Object> {

    static final Log logger = LogFactory.getLog(LogPipeline.class);

    private  FileWriter fileWriter;

    /**
     * 日志分为两种
     * agent日志: 默认在当前目录下的/logs下 可通过log.properties配置来指定
     * 采集日志：默认在被采集应用的根目录下/logs
     */
    public LogPipeline() {
        try {
            String defaultLog = System.getProperty("user.dir") + "/logs/apm-logs/";
            fileWriter =
                    new FileWriter(openFile(Agent.config.getProperty("log", defaultLog)), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File openFile(String rootDir) {
        Assert.hasText(rootDir, "log 设置错误");
        try {
            File root = new File(rootDir);
            if (!root.exists() || !root.isDirectory()) {
                root.mkdirs();
            }
            File file = new File(root, "apm-agent.log");
            if (file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void main(String[] args) throws IOException {
//        String defaultLog = System.getProperty("user.dir") + "/logs/";
//        Assert.hasText(defaultLog, "log 设置错误");
//        File file;
//        try {
//            File root = new File(defaultLog);
//            if (!root.exists() || !root.isDirectory()) {
//                root.mkdirs();
//            }
//            file = new File(root, "apm-agent.log");
//            if (file.exists()) {
//                file.createNewFile();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        new FileWriter(file).write(JSON.toJSONString("dwqdwqwd"));
//    }

    @Override
    public int order() {
        return 3;
    }

    @Override
    public Object accept(AgentSession agentSession, String o) {
        try {
            logger.info("写入日志-----:" + o);
            fileWriter.write(o + "\r\n");
            fileWriter.flush();
        } catch (IOException e) {
            logger.error("日志写入失败", e);
        }
        return null;
    }
}
