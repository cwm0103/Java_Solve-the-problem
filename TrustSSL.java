package cn.cc.dsp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * cwm
 * 2019-6-14
 * 解除SSL 认证的请求
 */
public class TrustSSL {

    private static Logger logger = LoggerFactory.getLogger(HttpRequest.class);
    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


    public static void SLLCWM(String url) throws Exception {
        URL console = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) console.openConnection();
        if (conn instanceof HttpsURLConnection)  {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
            ((HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());
            ((HttpsURLConnection) conn).setHostnameVerifier(new TrustAnyHostnameVerifier());
        }
        conn.connect();


        System.out.println(conn.getResponseCode());

    }

    /**
     * get 请求
     * @param httpurl
     * @return
     */
    public static String get(String httpurl){
        String message="";
        try {
            URL url=new URL(httpurl);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10*10000);

            if (connection instanceof HttpsURLConnection)  {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
                ((HttpsURLConnection) connection).setSSLSocketFactory(sc.getSocketFactory());
                ((HttpsURLConnection) connection).setHostnameVerifier(new TrustAnyHostnameVerifier());
            }
            connection.connect();
            InputStream inputStream=connection.getInputStream();

//            byte[] data=new byte[1024];
//            StringBuffer sb=new StringBuffer();
//            int length=0;
//            while ((length=inputStream.read(data))!=-1){
//                String s=new String(data, Charset.forName("utf-8"));
//                sb.append(s);
//            }




            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while( (len=inputStream.read(buffer)) != -1 ){
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();
            outStream.close();

            message =new String(data, Charset.forName("utf-8"));
            inputStream.close();
            connection.disconnect();
        } catch (Exception e) {
            logger.error("get有错："+e.toString());
            message="get有错："+e.toString();
            //e.printStackTrace();
        }
        return message;
    }

    /**
     * post 请求
     * @param httpUrl
     * @param params
     * @return
     */
    public static String post(String httpUrl,String params){
        String message="";
        try {
            URL url=new URL(httpUrl);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(10*30000);
            connection.setReadTimeout(10*30000);
            connection.setRequestProperty("Content-type","application/x-javascript->json");
            if (connection instanceof HttpsURLConnection)  {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
                ((HttpsURLConnection) connection).setSSLSocketFactory(sc.getSocketFactory());
                ((HttpsURLConnection) connection).setHostnameVerifier(new TrustAnyHostnameVerifier());
            }
            connection.connect();
            OutputStream outputStream=connection.getOutputStream();
//            StringBuffer sb=new StringBuffer();
//            sb.append("email=");
//            sb.append("409947972@qq.com&");
//            sb.append("password=");
//            sb.append("1234&");
//            sb.append("verify_code=");
//            sb.append("4fJ8");
//            String param=sb.toString();
            outputStream.write(params.getBytes());
            outputStream.flush();
            outputStream.close();
            logger.info("post输出","responseCode"+connection.getResponseCode());
            InputStream inputStream=connection.getInputStream();
            byte[] data=new byte[1024];
            StringBuffer sb1=new StringBuffer();
            int length=0;
            while ((length=inputStream.read(data))!=-1){
                String s=new String(data, Charset.forName("utf-8"));
                sb1.append(s);
            }
            message=sb1.toString();
            inputStream.close();
            connection.disconnect();
        } catch (Exception e) {
            logger.error("post有错："+e.toString());
            message="post有错："+e.toString();
        }
        return message;
    }
}
