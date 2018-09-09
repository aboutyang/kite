package com.aboutyang.config;

import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangwang【yangwang@jd.com】
 * @date 2018/8/24 17:36
 */
@ConfigurationProperties(prefix = "kite.http.logging")
public class RequestResponseLoggingProperties {

    /**
     * 是否开启请求响应报文的日志记录
     */
    private boolean enable;

    /**
     * 请求URL 中，traceKey 设置为 ture， 则记录请求和相应的所有信息。
     */
    private String traceKey = "traceMe";
    /**
     * 记录匹配的URL
     */
    private List<String> urlPatterns = Lists.newArrayList("/*");

    /**
     * 支持的 Content-Type 列表
     */
    private List<String> supportContentTypes = Lists.newArrayList("**");

    /**
     * 是否记录query string; 默认为 true， 记录
     */
    private boolean includeQueryString = true;

    /**
     * 是否记录客户信息; 默认为 true， 记录
     */
    private boolean includeClientInfo = true;

    /**
     * 是否记录请求头; 默认为 true， 记录
     */
    private boolean includeHeaders = true;

    /**
     * 是否记录请求体; 默认为 false, 不记录
     */
    private boolean includePayload = false;

    /**
     * 是否记录响应信息; 默认为 true， 记录
     */
    private boolean includeResponse = true;

    /**
     * 是否记录响应体; 默认为 false, 不记录
     */
    private boolean includeResponseBody = false;

    /**
     * 输出华丽的分割线; 默认为 ==============================
     */
    private String beautifyDividingLine = "==============================";

    /**
     * 记录请求内容(request body) 最大长度; 默认为 2000
     */
    private int maxPayloadLength = 2000;

    /**
     * 记录响应内容(response body) 最大长度; 默认为 2000
     */
    private int maxResponsePayloadLength = 2000;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getTraceKey() {
        return traceKey;
    }

    public void setTraceKey(String traceKey) {
        this.traceKey = traceKey;
    }

    public List<String> getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(List<String> urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public List<String> getSupportContentTypes() {
        return supportContentTypes;
    }

    public void setSupportContentTypes(List<String> supportContentTypes) {
        this.supportContentTypes = supportContentTypes;
    }

    public boolean isIncludeQueryString() {
        return includeQueryString;
    }

    public void setIncludeQueryString(boolean includeQueryString) {
        this.includeQueryString = includeQueryString;
    }

    public boolean isIncludeClientInfo() {
        return includeClientInfo;
    }

    public void setIncludeClientInfo(boolean includeClientInfo) {
        this.includeClientInfo = includeClientInfo;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public boolean isIncludePayload() {
        return includePayload;
    }

    public void setIncludePayload(boolean includePayload) {
        this.includePayload = includePayload;
    }

    public boolean isIncludeResponse() {
        return includeResponse;
    }

    public void setIncludeResponse(boolean includeResponse) {
        this.includeResponse = includeResponse;
    }

    public boolean isIncludeResponseBody() {
        return includeResponseBody;
    }

    public void setIncludeResponseBody(boolean includeResponseBody) {
        this.includeResponseBody = includeResponseBody;
    }

    public String getBeautifyDividingLine() {
        return beautifyDividingLine;
    }

    public void setBeautifyDividingLine(String beautifyDividingLine) {
        this.beautifyDividingLine = beautifyDividingLine;
    }

    public int getMaxPayloadLength() {
        return maxPayloadLength;
    }

    public void setMaxPayloadLength(int maxPayloadLength) {
        this.maxPayloadLength = maxPayloadLength;
    }

    public int getMaxResponsePayloadLength() {
        return maxResponsePayloadLength;
    }

    public void setMaxResponsePayloadLength(int maxResponsePayloadLength) {
        this.maxResponsePayloadLength = maxResponsePayloadLength;
    }
}
