package rocks.bastion.support.embedded;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.options.Option;
import com.mashape.unirest.http.options.Options;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestWithProxiedEmbeddedServer extends TestWithEmbeddedServer {

    private static HttpClient originalHttpClient;

    @BeforeClass
    public static void setupProxying() {
        DnsResolver dnsResolver = prepareProxiedDnsResolver();
        DefaultSchemePortResolver schemePortResolver = prepareSchemePortResolver();
        BasicHttpClientConnectionManager connManager = prepareConnectionManager(dnsResolver, schemePortResolver);
        HttpClient httpClient = prepareHttpClient(connManager);
        originalHttpClient = (HttpClient) Options.getOption(Option.HTTPCLIENT);
        Unirest.setHttpClient(httpClient);
    }

    @AfterClass
    public static void cleanupProxying() {
        Unirest.setHttpClient(originalHttpClient);
    }

    private static DefaultSchemePortResolver prepareSchemePortResolver() {
        return new DefaultSchemePortResolver() {
            @Override
            public int resolve(HttpHost host) throws UnsupportedSchemeException {
                if (host.getHostName().equalsIgnoreCase("sushi-shop.test")) {
                    return 9876;
                } else {
                    return super.resolve(host);
                }
            }
        };
    }

    private static CloseableHttpClient prepareHttpClient(BasicHttpClientConnectionManager connManager) {
        return HttpClientBuilder.create()
                .setConnectionManager(connManager)
                .build();
    }

    private static BasicHttpClientConnectionManager prepareConnectionManager(DnsResolver dnsResolver, DefaultSchemePortResolver schemePortResolver) {
        return new BasicHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", SSLConnectionSocketFactory.getSocketFactory())
                        .build(),
                null,
                schemePortResolver,
                dnsResolver
        );
    }

    private static DnsResolver prepareProxiedDnsResolver() {
        return new SystemDefaultDnsResolver() {
            @Override
            public InetAddress[] resolve(String host) throws UnknownHostException {
                if (host.equalsIgnoreCase("sushi-shop.test")) {
                    return new InetAddress[]{InetAddress.getByName("127.0.0.1")};
                } else {
                    return super.resolve(host);
                }
            }
        };
    }

}
