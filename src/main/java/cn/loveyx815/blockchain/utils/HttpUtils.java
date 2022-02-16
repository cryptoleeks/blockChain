package cn.loveyx815.blockchain.utils;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;


public class HttpUtils {

    /**
     * GET 请求, 兼容 HTTP 和 HTTPS 请求
     *
     * @param url
     *            请求的地址
     *
     * @return {@link HttpRequest}
     */
    public static HttpRequest get(String url) {
        return new HttpRequest(url, RequestMethod.GET);
    }

    /**
     * PUT 请求, 兼容 HTTP 和 HTTPS 请求
     *
     * @param url
     *            请求的地址
     *
     * @return {@link HttpRequest}
     */
    public static HttpRequest put(String url) {
        return new HttpRequest(url, RequestMethod.PUT);
    }

    /**
     * POST 请求, 兼容 HTTP 和 HTTPS 请求
     *
     * @param url
     *            请求的地址
     *
     * @return {@link HttpRequest}
     */
    public static HttpRequest post(String url) {
        return new HttpRequest(url, RequestMethod.POST);
    }

    /**
     * DELETE 请求, 兼容 HTTP 和 HTTPS 请求
     *
     * @param url
     *            请求的地址
     *
     * @return {@link HttpRequest}
     */
    public static HttpRequest delete(String url) {
        return new HttpRequest(url, RequestMethod.DELETE);
    }

    /**
     * 支持的请求方法
     *
     * @author fanlychie
     */
    private enum RequestMethod {GET, POST, PUT, DELETE}

    /**
     * 封装 HTTP 请求参数和操作
     *
     * @author fanlychie
     */
    public final static class HttpRequest {

        // 请求的地址
        private String url;

        // 请求体内容
        private String body;

        // 请求方法
        private RequestMethod method;

        // 失败重试的次数
        private int retryTimes = 3;

        // 读取结果超时的秒数
        private int readTimeoutSecond = 120;

        // 连接服务超时的秒数
        private int connectTimeoutSecond = 60;

        // 读取结果使用的字符集编码
        private String readResultCharset = "UTF-8";

        // 请求参数
        private Map<String, String> params;

        // 请求头参数
        private Map<String, String> header;

        // 代理端口
        private int proxyPort;

        // 代理主机
        private String proxyHost;

        // 代理类型(http或https)
        private String proxySchema;

        // 私有构造子
        private HttpRequest(String url, RequestMethod method) {
            this.url = url;
            this.method = method;
        }

        /**
         * 执行请求
         *
         * @param consumer
         *            (请求结果的状态码, 请求结果的文本内容)
         */
        public void execute(BiConsumer<Integer, String> consumer) {
            try {
                doService(consumer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 设置请求体, JSON 参数可设于此域
         *
         * @param body
         *            请求体内容
         *
         * @return {@link HttpRequest}
         */
        public HttpRequest setBody(String body) {
            this.body = body;
            return this;
        }

        /**
         * 设置失败重试的次数, 默认值 0
         *
         * @param retryTimes
         *            失败重试的次数
         *
         * @return {@link HttpRequest}
         */
        public HttpRequest setRetryTimes(int retryTimes) {
            this.retryTimes = retryTimes;
            return this;
        }

        /**
         * 设置读取结果超时的秒数, 默认 120s
         *
         * @param readTimeoutSecond
         *            读取结果超时的秒
         *
         * @return {@link HttpRequest}
         */
        public HttpRequest setReadTimeoutSecond(int readTimeoutSecond) {
            this.readTimeoutSecond = readTimeoutSecond;
            return this;
        }

        /**
         * 设置连接服务超时的秒数, 默认 60s
         *
         * @param connectTimeoutSecond
         *            连接服务超时的秒数
         *
         * @return {@link HttpRequest}
         */
        public HttpRequest setConnectTimeoutSecond(int connectTimeoutSecond) {
            this.connectTimeoutSecond = connectTimeoutSecond;
            return this;
        }

        /**
         * 设置请求参数表
         *
         * @param params
         *            请求参数表
         *
         * @return {@link HttpRequest}
         */
        @SuppressWarnings({ "rawtypes" })
        public HttpRequest setParams(Map params) {
            this.params = convertMap(params);
            return this;
        }

        /**
         * 设置请求头参数表
         *
         * @param header
         *            请求头参数表
         *
         * @return {@link HttpRequest}
         */
        @SuppressWarnings({ "rawtypes" })
        public HttpRequest setHeader(Map header) {
            this.header = convertMap(header);
            return this;
        }

        /**
         * 设置请求参数键值对
         *
         * @param nameValues
         *            请求参数键值对
         *
         * @return {@link HttpRequest}
         */
        public HttpRequest setParams(Object... nameValues) {
            this.params = convertMap(nameValues);
            return this;
        }

        /**
         * 设置请求头参数键值对
         *
         * @param nameValues
         *            请求头参数键值对
         *
         * @return {@link HttpRequest}
         */
        public HttpRequest setHeader(String... nameValues) {
            this.header = convertMap(nameValues);
            return this;
        }

        /**
         * 添加请求参数
         *
         * @param name
         *            参数名
         * @param value
         *            参数值
         * @return
         */
        public HttpRequest addParam(String name, Object value) {
            if (this.params == null) {
                this.params = new HashMap<>();
            }
            params.put(name, value.toString());
            return this;
        }

        /**
         * 添加请求头参数
         *
         * @param name
         *            参数名
         * @param value
         *            参数值
         * @return
         */
        public HttpRequest addHeader(String name, Object value) {
            if (this.header == null) {
                this.header = new HashMap<>();
            }
            header.put(name, value.toString());
            return this;
        }

        /**
         * 设置代理
         *
         * @param host
         *            代理主机
         * @param port
         *            代理端口
         *
         * @return {@link HttpRequest}
         */
        public HttpRequest setProxy(String host, int port) {
            if(StringUtils.isEmpty(host)){
                return this;
            }
            return setProxy(host, port, "http");
        }

        /**
         * 设置代理
         *
         * @param host
         *            代理主机
         * @param port
         *            代理端口
         * @param schema
         *            代理类型(http或https), 默认 http
         *
         * @return {@link HttpRequest}
         */
        public HttpRequest setProxy(String host, int port, String schema) {
            this.proxyHost = host;
            this.proxyPort = port;
            this.proxySchema = schema;
            return this;
        }

        /**
         * 设置读取结果使用的字符集编码
         *
         * @param readResultCharset
         *            读取结果使用的字符集编码, 默认 UTF-8
         *
         * @return {@link HttpRequest}
         */
        public HttpRequest setReadResultCharset(String readResultCharset) {
            this.readResultCharset = readResultCharset;
            return this;
        }

        /**
         * 执行请求
         *
         * @param consumer
         *            BiConsumer
         *
         * @throws Exception
         */
        private void doService(BiConsumer<Integer, String> consumer) throws Exception {
            try (CloseableHttpClient client = getHttpClient()) {
                HttpUriRequest request = getHttpUriRequest();
                // 执行请求
                HttpResponse response = client.execute(request);
                // 状态码
                int statusCode = response.getStatusLine().getStatusCode();
                // 响应内容
                String responseText = readStream(response.getEntity().getContent());
                // Consumer
                consumer.accept(statusCode, responseText);
            }
        }

        /**
         * 转换参数为散列表
         *
         * @param obj
         *            被转换的参数
         *
         * @return Map
         */
        private Map<String, String> convertMap(Object obj) {
            Map<String, String> target = new HashMap<>();
            if (obj instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) obj;
                map.forEach((k, v) -> target.put(k.toString(), v.toString()));
            }
            else if (obj.getClass().isArray()) {
                Object[] array = (Object[]) obj;
                IntStream.range(0, array.length).filter((i) -> i % 2 == 0)
                        .forEach((i) -> target.put(array[i].toString(), array[i + 1].toString()));
            }
            return target;
        }

        /**
         * 构建 HttpUriRequest
         *
         * @param base
         *            HttpUriRequest
         *
         * @return HttpUriRequest
         */
        private HttpUriRequest buildHttpUriRequest(HttpUriRequest base) {
            if (header != null && !header.isEmpty()) {
                header.forEach((k, v) -> base.addHeader(k, v));
            }
            return base;
        }

        /**
         * 构建 HttpUriRequest
         *
         * @param base
         *            HttpEntityEnclosingRequestBase
         *
         * @return HttpUriRequest
         *
         * @throws Exception
         */
        private HttpUriRequest buildHttpUriRequest(HttpEntityEnclosingRequestBase base) throws Exception {
            if (header == null) { header = new HashMap<>(); }
            // 处理请求参数
            if (params != null && !params.isEmpty()) {
                // 参数键值对数组
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                params.forEach((k, v) -> nameValuePairs.add(new BasicNameValuePair(k, v)));
                // 如果有参数, 设置请求参数
                if (!nameValuePairs.isEmpty()) {
                    base.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                    header.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8");
                }
            }
            // 处理请求参数
            else if (body != null && !body.isEmpty()) {
                base.setEntity(new StringEntity(body, "UTF-8"));
                header.put(HttpHeaders.ACCEPT, "application/json");
                header.put(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
            }
            return buildHttpUriRequest((HttpUriRequest) base);
        }

        /**
         * 构建 HttpUriRequest
         *
         * @return HttpUriRequest
         *
         * @throws Exception
         */
        private HttpUriRequest buildHttpUriRequest() throws Exception {
            if (params != null && !params.isEmpty()) {
                StringBuilder paramStr = new StringBuilder();
                params.forEach((k, v) -> {
                    if (paramStr.length() > 0) {
                        paramStr.append("&");
                    }
                    paramStr.append(k).append("=").append(encode(v));
                });
                if (paramStr.length() > 0) {
                    if (url.contains("?")) {
                        url += "&" + paramStr;
                    } else {
                        url += "?" + paramStr;
                    }
                }
            }
            return buildHttpUriRequest(RequestBuilder.get().setUri(url).build());
        }

        /**
         * 获取 HttpUriRequest 对象
         *
         * @return HttpUriRequest
         *
         * @throws Exception
         */
        private HttpUriRequest getHttpUriRequest() throws Exception {
            switch (method) {
                case GET:
                    return buildHttpUriRequest();
                case PUT:
                    return buildHttpUriRequest(new HttpPut(url));
                case POST:
                    return buildHttpUriRequest(new HttpPost(url));
                case DELETE:
                    return buildHttpUriRequest(new HttpDelete(url));
                default:
                    throw new UnsupportedOperationException("Unsupport " + method + " request.");
            }
        }

        /**
         * 编码字符串
         *
         * @param str
         *            源字串
         *
         * @return 返回UTF-8编码的内容
         */
        private String encode(String str) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 读取流内容
         *
         * @param inStream
         *            输入流
         * @return
         * @throws IOException
         */
        private String readStream(InputStream inStream) throws Exception {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, readResultCharset))) {
                String tempStr = null;
                StringBuilder builder = new StringBuilder();
                while ((tempStr = reader.readLine()) != null) {
                    builder.append(tempStr);
                }
                return builder.toString();
            }
        }

        /**
         * 获取客户端请求对象
         *
         * @return CloseableHttpClient
         *
         * @throws Exception
         */
        private CloseableHttpClient getHttpClient() throws Exception {
            Builder requestConfigBuilder = RequestConfig.custom()
                    // 设置连接超时
                    .setConnectTimeout(connectTimeoutSecond * 1000)
                    // 设置读取超时
                    .setSocketTimeout(readTimeoutSecond * 1000);
            // 设置代理
            if (proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0) {
                requestConfigBuilder.setProxy(new HttpHost(proxyHost, proxyPort, proxySchema));
            }
            HttpClientBuilder builder = HttpClientBuilder.create()
                    // 设置请求配置
                    .setDefaultRequestConfig(requestConfigBuilder.build())
                    // 设置失败重试
                    .setRetryHandler(new StandardHttpRequestRetryHandler(retryTimes, true));
            // 是否使用SSL协议链接
            if (url.startsWith("https")) {
                builder.setSSLSocketFactory(new SSLConnectionSocketFactory(
                        new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                            // 信任所有
                            public boolean isTrusted(X509Certificate[] chain, String authType)
                                    throws CertificateException {
                                return true;
                            }
                        }).build()));
            }
            return builder.build();
        }

    }
}
