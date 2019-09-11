package com.juning.rect.reactor;

/**
 * @author yanjun
 */
public interface LifeCycle {

    /**
     * 启动rect
     */
    void start();

    /**
     * 停止rect
     */
    void stop();


    /**
     * 注册shutdownHook，完成清理资源
     * @param shutdownHook
     */
    default void registerShutdownHook(Thread shutdownHook) {
        if (shutdownHook == null) {
            shutdownHook = new Thread(() -> {
                stop();
            });
        }
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }
}
