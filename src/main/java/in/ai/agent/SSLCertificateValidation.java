package in.ai.agent;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * Utility class to disable SSL certificate validation.
 * WARNING: This is insecure and should only be used for local development
 * to bypass PKIX path building errors. Do not use in production.
 */
public class SSLCertificateValidation {

    private static SSLContext insecureSslContext = null;

    private static void initialize() {
        if (insecureSslContext != null) {
            return;
        }
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            insecureSslContext = sc;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create insecure SSL context", e);
        }
    }

    public static SSLContext getInsecureSslContext() {
        if (insecureSslContext == null) {
            initialize();
        }
        return insecureSslContext;
    }

    public static void disable() {
        if (insecureSslContext == null) {
            initialize();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(insecureSslContext.getSocketFactory());
        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        System.out.println("WARNING: Global SSL certificate validation has been disabled for HttpsURLConnection. This is for development only.");
    }
}