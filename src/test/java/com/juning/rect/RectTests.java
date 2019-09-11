package com.juning.rect;

import com.alibaba.fastjson.JSON;
import com.juning.rect.common.RectException;
import com.juning.rect.model.Request;
import com.juning.rect.model.Response;
import com.juning.rect.reactor.CallBack;
import com.juning.rect.reactor.config.RectClientConfig;
import com.juning.rect.reactor.config.RectServerConfig;
import com.juning.rect.reactor.support.DefaultRectClient;
import com.juning.rect.reactor.support.DefaultRectServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RunWith(JUnit4.class)
public class RectTests {
    private DefaultRectServer rectServer;
    private DefaultRectClient rectClient;
    private RectServerConfig serverConfig;
    private RectClientConfig clientConfig;

    @Before
    public void startRect() {
        serverConfig = new RectServerConfig();
        serverConfig.setHost("localhost");
        serverConfig.setPort(8848);
        serverConfig.setMainEventLoopGroupThreads(1);
        serverConfig.setSubEventLoopGroupThreads(2);
        serverConfig.setTaskExectorGroupThreads(4);
        rectServer = new DefaultRectServer(serverConfig);

        new Thread(() -> {
            rectServer.start();
        }).start();

        clientConfig = new RectClientConfig();
        clientConfig.setServerHost(serverConfig.getHost());
        clientConfig.setServerPort(serverConfig.getPort());
        clientConfig.setConnectTimeOutMills(1000 * 10);
        clientConfig.setSelectorEventLoopGroupThreads(1);
        clientConfig.setTaskEventLoopGroupThreads(2);
        rectClient = new DefaultRectClient(clientConfig);
        rectClient.start();
    }

    @Test
    public void invoke() throws IOException, RectException, InterruptedException {
        String address = serverConfig.getHost() + ":" + serverConfig.getPort();
        Response r = rectClient.invokeSync(address, new Request("Hello World"), 60, TimeUnit.SECONDS);
        System.out.println("同步调用响应:" + JSON.toJSONString(r));
        rectClient.invokeAsync(address, new Request("Hello Earth"), 60, TimeUnit.SECONDS, new CallBack<Response>() {
            @Override
            public void call(Response reponse) {
                System.out.println("异步调用响应:" + JSON.toJSONString(reponse));
            }
        });
        rectClient.invokeOneway(address, new Request("Hello Sun"));
        Thread.sleep(1000 * 2);
    }

    @After
    public void stopRect() {
        if (rectServer != null) {
            rectServer.stop();
        }
        if (rectClient != null) {
            rectClient.stop();
        }
    }
}
