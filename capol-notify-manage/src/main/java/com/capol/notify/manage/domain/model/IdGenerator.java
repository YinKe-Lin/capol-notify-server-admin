package com.capol.notify.manage.domain.model;

import com.capol.notify.manage.domain.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IdGenerator {
    private static volatile SnowflakeIdWorker instance;

    private IdGenerator() {
    }

    public static SnowflakeIdWorker getInstance() {
        if (instance == null) {
            synchronized (SnowflakeIdWorker.class) {
                if (instance == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("ID生成器启动!!!");
                    }
                    instance = new SnowflakeIdWorker();
                }
            }
        }
        return instance;
    }

    public static Long generateId() {
        SnowflakeIdWorker idWorker = getInstance();

        try {
            if (idWorker.isGenerateByLocal()) {
                return idWorker.nextId();
            }
        } catch (Exception e) {
            log.error("ID生成异常, 来自生成器: id-generator-server , 异常内容：{} ", e.getMessage());
            e.printStackTrace();
        }
        return idWorker.nextId();
    }

    /**
     * 设置机器码，增加取余操作，保证不大于15
     *
     * @param machineCode 机器码
     */
    public void setMachineCode(long machineCode) {
        // 取余，保证输入不会大于15
        instance.setMachineCode(machineCode);
    }

    public static void setGenerateByLocal(boolean generateByLocal) {
        instance.setGenerateByLocal(generateByLocal);
    }
}
