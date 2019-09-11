package com.juning.rect;

import com.juning.rect.reactor.config.RectServerConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.juning.rect.reactor.config.RectClientConfig;
import com.juning.rect.reactor.support.DefaultRectClient;
import com.juning.rect.reactor.support.DefaultRectServer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * @author yanjun
 */
@RunWith(JUnit4.class)
public class RectConnectionTests {
    private DefaultRectServer rectServer;
    private DefaultRectClient rectClient;

    RectServerConfig serverConfig;
    RectClientConfig clientConfig;

    @Before
    public void startUp() {
        serverConfig = new RectServerConfig();
        serverConfig.setHost("localhost");
        serverConfig.setPort(8848);
        serverConfig.setMainEventLoopGroupThreads(1);
        serverConfig.setSubEventLoopGroupThreads(2);
        serverConfig.setTaskExectorGroupThreads(2);
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
    public void connect() throws IOException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(new ConThread(8848));
        //service.submit(new ConThread(8849));
        //service.submit(new ConThread(8850));
        System.in.read();
    }

    class ConThread implements Runnable {
        int port;

        public ConThread(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 50000; i++) {
//                    rectClient.bootstrap.connect(clientConfig.getServerHost(), port).sync();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}