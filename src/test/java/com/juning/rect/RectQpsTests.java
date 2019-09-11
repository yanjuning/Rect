package com.juning.rect;

import com.juning.rect.model.Response;
import com.juning.rect.reactor.config.RectServerConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.juning.rect.common.RectException;
import com.juning.rect.model.Request;
import com.juning.rect.reactor.RectClient;
import com.juning.rect.reactor.RectServer;
import com.juning.rect.reactor.config.RectClientConfig;
import com.juning.rect.reactor.support.DefaultRectClient;
import com.juning.rect.reactor.support.DefaultRectServer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yanjun
 */
@RunWith(JUnit4.class)
public class RectQpsTests {

    private RectServer rectServer;
    private RectClient rectClient;

    private BufferedWriter bufferedWriter;
    private long counter;

    RectServerConfig serverConfig;

    @Before
    public void startRect() throws IOException {
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

        RectClientConfig clientConfig = new RectClientConfig();
        clientConfig.setServerHost(serverConfig.getHost());
        clientConfig.setServerPort(serverConfig.getPort());
        clientConfig.setConnectTimeOutMills(1000 * 10);
        clientConfig.setSelectorEventLoopGroupThreads(1);
        clientConfig.setTaskEventLoopGroupThreads(2);
        rectClient = new DefaultRectClient(clientConfig);
        rectClient.start();

        bufferedWriter = new BufferedWriter(new FileWriter(new File("D:\\rectqps.log")));
    }

    @Test
    public void invokeSync() throws IOException, RectException, InterruptedException {
        int CHARKBSIZE = 512;
        int size = CHARKBSIZE * 100;
        char[] data = new char[size];
        String fixSizeStr = new String(data);

        int threads = 4;
        CountDownLatch latch = new CountDownLatch(threads);
        ExecutorService service = Executors.newFixedThreadPool(8);
        bufferedWriter.write(String.valueOf(System.currentTimeMillis()));
        for (int i = 0; i < threads; i++) {
            service.submit(new ReqThread(latch, fixSizeStr, 1000 * 10));
        }
        latch.await();
        bufferedWriter.write(String.valueOf(System.currentTimeMillis()));
        bufferedWriter.close();
        System.err.println("counter=" + counter);
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

    class ReqThread implements Runnable {
        CountDownLatch latch;
        AtomicInteger index = new AtomicInteger(0);
        String req;
        long duration = 0;

        String address = serverConfig.getHost() + ":" + serverConfig.getPort();

        public ReqThread(CountDownLatch latch, String req, long duration) {
            this.duration = duration;
            this.latch = latch;
            this.req = req;
        }

        @Override
        public void run() {
            try {
                long start = System.currentTimeMillis();
                boolean timeUp = false;
                long tid = Thread.currentThread().getId();
                while (!timeUp) {
                    Response r = rectClient.invokeSync(address,
                            new Request(String.format("TID-%d, RID-%d, REQ-%s", tid, index.incrementAndGet(), req)),
                            60,
                            TimeUnit.SECONDS);
                    bufferedWriter.write(r.toString());
                    bufferedWriter.newLine();
                    if (System.currentTimeMillis() - start >= duration) {
                        timeUp = true;
                    }
                }
                synchronized (RectQpsTests.class) {
                    counter += index.get();
                }
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
