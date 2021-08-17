package io.kings.framework.util;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * <p>
 * 获取本地IP
 * </p>
 *
 * @author lun.wang
 * @date 2021/6/28 3:13 下午
 * @since v1.0
 */
public class IpUtil {
    private static final String ANY_HOST = "0.0.0.0";
    private static final String LOCALHOST = "127.0.0.1";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
    private static InetAddress localAddress = null;

    private static class NetworkException extends RuntimeException {
        public NetworkException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private IpUtil() {
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address != null && !address.isLoopbackAddress()) {
            String name = address.getHostAddress();
            return name != null && !ANY_HOST.equals(name) && !LOCALHOST.equals(name) &&
                    IP_PATTERN.matcher(name).matches();
        } else {
            return false;
        }
    }

    private static InetAddress getFirstValidAddress() {
        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    NetworkInterface network = interfaces.nextElement();
                    Enumeration<InetAddress> addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (isValidAddress(address)) {
                            return address;
                        }
                    }
                }
            }
        } catch (Throwable var8) {
            throw new NetworkException("failure to fetch local InetAddress", var8);
        }
        return localAddress;
    }

    private static InetAddress getAddress() {
        if (localAddress == null) {
            localAddress = getFirstValidAddress();
        }
        return localAddress;
    }

    public static String getIp() {
        InetAddress address = getAddress();
        assert address != null;
        return address.getHostAddress();
    }
}
