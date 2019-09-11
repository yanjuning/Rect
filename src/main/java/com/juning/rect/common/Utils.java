package com.juning.rect.common;

import java.net.InetSocketAddress;

/**
 * @author yanjun
 */
public class Utils {

    public static InetSocketAddress toInetSocketAddress(String address) throws RectException {
        try {
            String[] parts = address.split(":");
            return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
        } catch (Exception e) {
            throw new RectException(String.format("address[ip:port] %s cannot convert to InetSocketAddress", address), e);
        }
    }
}
