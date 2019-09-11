package com.juning.rect.reactor.config;

/**
 * @author yanjun
 */
public class RectClientConfig {
    private int selectorEventLoopGroupThreads;
    private int taskEventLoopGroupThreads;
    private Integer connectTimeOutMills;
    private boolean keepAlived = true;
    private String serverHost;
    private int serverPort;
    private int maxConnections = 1;
    private int maxAsyncRequest = 1;
    private int maxOnewayRequest = 1;
    private int readerIdleTimeSeconds = 60;
    private int writerIdleTimeSeconds = 60;

    public int getSelectorEventLoopGroupThreads() {
        return selectorEventLoopGroupThreads;
    }

    public void setSelectorEventLoopGroupThreads(int selectorEventLoopGroupThreads) {
        this.selectorEventLoopGroupThreads = selectorEventLoopGroupThreads;
    }

    public int getTaskEventLoopGroupThreads() {
        return taskEventLoopGroupThreads;
    }

    public void setTaskEventLoopGroupThreads(int taskEventLoopGroupThreads) {
        this.taskEventLoopGroupThreads = taskEventLoopGroupThreads;
    }

    public Integer getConnectTimeOutMills() {
        return connectTimeOutMills;
    }

    public void setConnectTimeOutMills(Integer connectTimeOutMills) {
        this.connectTimeOutMills = connectTimeOutMills;
    }

    public boolean isKeepAlived() {
        return keepAlived;
    }

    public void setKeepAlived(boolean keepAlived) {
        this.keepAlived = keepAlived;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaxAsyncRequest() {
        return maxAsyncRequest;
    }

    public void setMaxAsyncRequest(int maxAsyncRequest) {
        this.maxAsyncRequest = maxAsyncRequest;
    }

    public int getMaxOnewayRequest() {
        return maxOnewayRequest;
    }

    public void setMaxOnewayRequest(int maxOnewayRequest) {
        this.maxOnewayRequest = maxOnewayRequest;
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
