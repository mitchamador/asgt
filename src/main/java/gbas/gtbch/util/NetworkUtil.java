package gbas.gtbch.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.stream.Stream;

import static gbas.gtbch.util.EnumerationUtil.enumerationAsStream;

/**
 * network utility class
 */
public class NetworkUtil {

    private static Stream<InetAddress> getNonLocalIpAddresses(String startsWith) throws IOException {
        return enumerationAsStream(NetworkInterface.getNetworkInterfaces())
                .flatMap(networkInterface -> enumerationAsStream(networkInterface.getInetAddresses()))
                .filter(inetAddress -> inetAddress.getHostAddress().startsWith(startsWith))
                ;
    }

    /**
     * get network address with default startsWith ('10.*')
     * @return
     * @throws IOException
     */
    public static String getInetAddress() throws IOException {
        return getInetAddress("10.");
    }

    /**
     * get network address
     * @param startsWith address startsWith
     * @return
     * @throws IOException
     */
    public static String getInetAddress(String startsWith) throws IOException {
        return getNonLocalIpAddresses(startsWith)
                .map(InetAddress::getHostAddress)
                .findFirst()
                .orElse(InetAddress.getLocalHost().getHostAddress());
    }

}
