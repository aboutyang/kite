package com.aboutyang.common.filter;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author yangwang【yangwang@jd.com】
 * @date 2018/8/24 16:43
 */
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    private PathMatcher pathMatcher = new AntPathMatcher();

    public static final String DEFAULT_BEFORE_MESSAGE_PREFIX = "Request Start";

    public static final String DEFAULT_BEFORE_MESSAGE_SUFFIX = "Request End";

    public static final String DEFAULT_AFTER_MESSAGE_PREFIX = "Response Start";

    public static final String DEFAULT_AFTER_MESSAGE_SUFFIX = "Response End";

    private static final String DEFAULT_BEAUTIFY_DIVIDING_LINE = "--------------------------------------";

    private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 2000;

    private String traceKey;

    private List<String> urlPatterns;

    private List<String> supportContentTypes;

    private boolean includeQueryString = false;

    private boolean includeClientInfo = false;

    private boolean includeHeaders = false;

    private boolean includePayload = false;

    private boolean includeResponse = false;

    private boolean includeResponseBody = false;

    private int maxPayloadLength = DEFAULT_MAX_PAYLOAD_LENGTH;

    private int maxResponsePayloadLength = DEFAULT_MAX_PAYLOAD_LENGTH;

    private String beforeMessagePrefix = DEFAULT_BEFORE_MESSAGE_PREFIX;

    private String beforeMessageSuffix = DEFAULT_BEFORE_MESSAGE_SUFFIX;

    private String afterMessagePrefix = DEFAULT_AFTER_MESSAGE_PREFIX;

    private String afterMessageSuffix = DEFAULT_AFTER_MESSAGE_SUFFIX;

    private String beautifyDividingLine = DEFAULT_BEAUTIFY_DIVIDING_LINE;

    public void setBeautifyDividingLine(String beautifyDividingLine) {
        this.beautifyDividingLine = beautifyDividingLine;
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

    /**
     * Set whether the query string should be included in the log message.
     * <p>Should be configured using an {@code <init-param>} for parameter name
     * "includeQueryString" in the filter definition in {@code web.xml}.
     */
    public void setIncludeQueryString(boolean includeQueryString) {
        this.includeQueryString = includeQueryString;
    }

    /**
     * Return whether the query string should be included in the log message.
     */
    protected boolean isIncludeQueryString() {
        return this.includeQueryString;
    }

    /**
     * Set whether the client address and session id should be included in the
     * log message.
     * <p>Should be configured using an {@code <init-param>} for parameter name
     * "includeClientInfo" in the filter definition in {@code web.xml}.
     */
    public void setIncludeClientInfo(boolean includeClientInfo) {
        this.includeClientInfo = includeClientInfo;
    }

    /**
     * Return whether the client address and session id should be included in the
     * log message.
     */
    protected boolean isIncludeClientInfo() {
        return this.includeClientInfo;
    }

    /**
     * Set whether the request headers should be included in the log message.
     * <p>Should be configured using an {@code <init-param>} for parameter name
     * "includeHeaders" in the filter definition in {@code web.xml}.
     *
     * @since 4.3
     */
    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    /**
     * Return whether the request headers should be included in the log message.
     *
     * @since 4.3
     */
    protected boolean isIncludeHeaders() {
        return this.includeHeaders;
    }

    /**
     * Set whether the request payload (body) should be included in the log message.
     * <p>Should be configured using an {@code <init-param>} for parameter name
     * "includePayload" in the filter definition in {@code web.xml}.
     *
     * @since 3.0
     */
    public void setIncludePayload(boolean includePayload) {
        this.includePayload = includePayload;
    }

    /**
     * Return whether the request payload (body) should be included in the log message.
     *
     * @since 3.0
     */
    protected boolean isIncludePayload() {
        return this.includePayload;
    }

    /**
     * Set the maximum length of the payload body to be included in the log message.
     * Default is 50 characters.
     *
     * @since 3.0
     */
    public void setMaxPayloadLength(int maxPayloadLength) {
        Assert.isTrue(maxPayloadLength >= 0, "'maxPayloadLength' should be larger than or equal to 0");
        this.maxPayloadLength = maxPayloadLength;
    }

    /**
     * Return the maximum length of the payload body to be included in the log message.
     *
     * @since 3.0
     */
    protected int getMaxPayloadLength() {
        return this.maxPayloadLength;
    }

    /**
     * Set the value that should be prepended to the log message written
     * <i>before</i> a request is processed.
     */
    public void setBeforeMessagePrefix(String beforeMessagePrefix) {
        this.beforeMessagePrefix = beforeMessagePrefix;
    }

    /**
     * Set the value that should be appended to the log message written
     * <i>before</i> a request is processed.
     */
    public void setBeforeMessageSuffix(String beforeMessageSuffix) {
        this.beforeMessageSuffix = beforeMessageSuffix;
    }

    /**
     * Set the value that should be prepended to the log message written
     * <i>after</i> a request is processed.
     */
    public void setAfterMessagePrefix(String afterMessagePrefix) {
        this.afterMessagePrefix = afterMessagePrefix;
    }

    /**
     * Set the value that should be appended to the log message written
     * <i>after</i> a request is processed.
     */
    public void setAfterMessageSuffix(String afterMessageSuffix) {
        this.afterMessageSuffix = afterMessageSuffix;
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

    public int getMaxResponsePayloadLength() {
        return maxResponsePayloadLength;
    }

    public void setMaxResponsePayloadLength(int maxResponsePayloadLength) {
        this.maxResponsePayloadLength = maxResponsePayloadLength;
    }

    /**
     * The default value is "false" so that the filter may log a "before" message
     * at the start of request processing and an "after" message at the end from
     * when the last asynchronously dispatched thread is exiting.
     */
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    /**
     * Forwards the request to the next filter in the chain and delegates down to the subclasses
     * to perform the actual request logging both before and after the request is processed.
     *
     * @see #loggingMessage
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        StopWatch stopWatch = new StopWatch("request and response monitor");
        stopWatch.start("Step 1");
        boolean traceMe = false;
        if (StringUtils.isNotBlank(traceKey)) {
            traceMe = BooleanUtils.toBoolean(request.getParameter(traceKey));
        }
        //traceMe 为 true， urlMatch 不用计算
        boolean urlMatch = traceMe || (urlPatterns != null && urlPatterns.stream().anyMatch(urlPattern -> pathMatcher.match(urlPattern, request.getRequestURI())));
        if (urlMatch) {
            boolean isFirstRequest = !isAsyncDispatch(request);
            HttpServletRequest requestToUse = request;
            HttpServletResponse responseToUse = response;

            if ((traceMe || isIncludePayload()) && isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
                requestToUse = new ContentCachingRequestWrapper(request, getMaxPayloadLength());
            }

            if ((traceMe || isIncludeResponse()) && isFirstRequest && !(response instanceof ContentCachingResponseWrapper)) {
                responseToUse = new ContentCachingResponseWrapper(response);
            }

            boolean shouldLog = traceMe || shouldLog(requestToUse);
            if (shouldLog && isFirstRequest) {
                String requestMessage = createMessage(requestToUse, this.beforeMessagePrefix + " [step 1]", this.beforeMessageSuffix + " [step 1]", traceMe);
                loggingMessage(requestMessage);
            }
            try {
                stopWatch.stop();
                stopWatch.start("Step 2");
                filterChain.doFilter(requestToUse, responseToUse);
                stopWatch.stop();
                stopWatch.start("Step 3");
            } finally {
                if (shouldLog && !isAsyncStarted(requestToUse)) {
                    String requestMessage = createMessage(requestToUse, this.beforeMessagePrefix + " [step 2]", this.beforeMessageSuffix + " [step 2]", traceMe);
                    String responseMessage = createMessage(responseToUse, this.afterMessagePrefix + " [step 3]", this.afterMessageSuffix + " [step 3]", traceMe);
                    loggingMessage(requestMessage);
                    loggingMessage(responseMessage);
                    stopWatch.stop();
                    loggingMessage(stopWatch.prettyPrint());
                }
                if (responseToUse instanceof ContentCachingResponseWrapper) {
                    ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(responseToUse, ContentCachingResponseWrapper.class);
                    wrapper.copyBodyToResponse();
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Create a log message for the given request, prefix and suffix.
     * <p>If {@code includeQueryString} is {@code true}, then the inner part
     * of the log message will take the form {@code request_uri?query_string};
     * otherwise the message will simply be of the form {@code request_uri}.
     * <p>The final message is composed of the inner part as described and
     * the supplied prefix and suffix.
     */
    protected String createMessage(HttpServletRequest request, String prefix, String suffix, boolean traceMe) {
        StringBuilder msg = new StringBuilder();
        msg.append("\n").append(beautifyDividingLine).append(prefix).append(beautifyDividingLine).append("\n");
        msg.append("uri=").append(request.getRequestURI());

        if (traceMe || isIncludeQueryString()) {
            String queryString = request.getQueryString();
            if (queryString != null) {
                msg.append('?').append(queryString);
            }
        }
        msg.append("\n");

        msg.append("content-type=").append(request.getContentType()).append("\n");
        msg.append("encoding=").append(request.getCharacterEncoding()).append("\n");
        msg.append("local=").append(request.getLocale().toString()).append("\n");

        if (traceMe || isIncludeClientInfo()) {
            String client = request.getRemoteAddr();
            if (StringUtils.isNotBlank(client)) {
                msg.append("client=").append(client).append("\n");
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                msg.append("session=").append(session.getId()).append("\n");
            }
            String user = request.getRemoteUser();
            if (user != null) {
                msg.append("user=").append(user).append("\n");
            }
        }

        if (traceMe || isIncludeHeaders()) {
            msg.append("headers=").append(new ServletServerHttpRequest(request).getHeaders()).append("\n");
        }

        if (traceMe || (isIncludePayload() && isSupportRequest(request))) {
            String payload = getMessagePayload(request);
            if (payload != null) {
                msg.append("payload=").append(payload).append("\n");
            }
        }
        msg.append(beautifyDividingLine).append(suffix).append(beautifyDividingLine);
        return msg.toString();
    }


    protected String createMessage(HttpServletResponse response, String prefix, String suffix, boolean traceMe) {
        StringBuilder msg = new StringBuilder();
        msg.append("\n").append(beautifyDividingLine).append(prefix).append(beautifyDividingLine).append("\n");
        msg.append("status=").append(response.getStatus()).append("\n");
        msg.append("content-type=").append(response.getContentType()).append("\n");
        msg.append("encoding=").append(response.getCharacterEncoding()).append("\n");
        msg.append("local=").append(response.getLocale().toString()).append("\n");
        msg.append("headers=[\n");
        response.getHeaderNames().stream().forEachOrdered(headerName -> {
            msg.append("\t").append(headerName).append("=").append(response.getHeaders(headerName)).append("\n");
        });
        msg.append("]");
        if (traceMe || (isIncludeResponseBody() && isSupportResponse(response))) {
            msg.append("body=").append(getMessagePayload(response)).append("\n");
        }
        msg.append(beautifyDividingLine).append(suffix).append(beautifyDividingLine);
        return msg.toString();
    }

    /**
     * Extracts the message payload portion of the message created by
     * {@link #createMessage(HttpServletRequest, String, String, boolean)} when
     * {@link #isIncludePayload()} returns true.
     *
     * @since 5.0.3
     */
    @Nullable
    protected String getMessagePayload(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper =
                WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, getMaxPayloadLength());
                try {
                    return new String(buf, 0, length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    return "[unknown]";
                }
            }
        }
        return null;
    }

    @Nullable
    protected String getMessagePayload(HttpServletResponse response) {
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, getMaxResponsePayloadLength());
                try {
                    return new String(buf, 0, length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    return "[unknown]";
                }
            }
        }
        return null;
    }


    /**
     * Determine whether to call the {@link #loggingMessage}
     * methods for the current request, i.e. whether logging is currently active
     * (and the log message is worth building).
     * <p>The default implementation always returns {@code true}. Subclasses may
     * override this with a log level check.
     *
     * @param request current HTTP request
     * @return {@code true} if the before/after method should get called;
     * {@code false} otherwise
     * @since 4.1.5
     */
    protected boolean shouldLog(HttpServletRequest request) {
        return log.isDebugEnabled();
    }

    /**
     * Concrete subclasses should implement this method to write a log message
     * <i>before</i> the request is processed.
     *
     * @param message the message to log
     */
    protected void loggingMessage(String message) {
        if (logger.isDebugEnabled()) {
            log.debug(message);
        } else {
            System.out.println(message);
        }
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

    public boolean isSupportRequest(HttpServletRequest request) {
        if (supportContentTypes == null) {
            return false;
        }
        if (StringUtils.isBlank(request.getContentType())) {
            log.warn("request content type is bank");
            return false;
        } else {
            return supportContentTypes.stream().anyMatch(contentType -> StringUtils.containsIgnoreCase(request.getContentType(), contentType));
        }
    }

    public boolean isSupportResponse(HttpServletResponse response) {
        if (supportContentTypes == null) {
            return false;
        }
        if (StringUtils.isBlank(response.getContentType())) {
            log.warn("response content type is bank");
            return false;
        } else {
            return supportContentTypes.stream().anyMatch(contentType -> StringUtils.containsIgnoreCase(response.getContentType(), contentType));
        }

    }
}
