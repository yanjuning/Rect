package com.juning.rect.reactor.config;

/**
 * @author yanjun
 */
public class RectServerConfig {
    private int mainEventLoopGroupThreads = 1;
    private int subEventLoopGroupThreads = Math.min(Runtime.getRuntime().availableProcessors(), 8);
    private int taskExectorGroupThreads = Math.min(Runtime.getRuntime().availableProcessors(), 32);
    private String host = "localhost";
    private int port = 8848;
    private int readerIdleTimeSeconds = 60;
    private int writerIdleTimeSeconds = 60;

    public int getMainEventLoopGroupThreads() {
        return mainEventLoopGroupThreads;
    }

    public void setMainEventLoopGroupThreads(int mainEventLoopGroupThreads) {
        this.mainEventLoopGroupThreads = mainEventLoopGroupThreads;
    }

    public int getSubEventLoopGroupThreads() {
        return subEventLoopGroupThreads;
    }

    public void setSubEventLoopGroupThreads(int subEventLoopGroupThreads) {
        this.subEventLoopGroupThreads = subEventLoopGroupThreads;
    }

    public int getTaskExectorGroupThreads() {
        return taskExectorGroupThreads;
    }

    public void setTaskExectorGroupThreads(int taskExectorGroupThreads) {
        this.taskExectorGroupThreads = taskExectorGroupThreads;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getReaderIdleTimeSeconds() {
        return readerIdleTimeSeconds;
    }

    public void setReaderIdleTimeSeconds(int readerIdleTimeSeconds) {
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
    }

    public int getWriterIdleTimeSeconds() {
        return writerIdleTimeSeconds;
    }

    public void setWriterIdleTimeSeconds(int writerIdleTimeSeconds) {
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
    }
}
