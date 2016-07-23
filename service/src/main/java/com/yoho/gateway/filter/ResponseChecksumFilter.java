package com.yoho.gateway.filter;

import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 对响应的body添加MD5
 */
public class ResponseChecksumFilter extends OncePerRequestFilter {

    private static final String HEADER_CHECKSUM = "X-YH-Response-Checksum";

    private static final String MD5_SALT = "fd4ad5fcsa0de589af23234ks1923ks";


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        /****  不处理下面的情况   **/
        String path = request.getRequestURI();
        if(path.contains("hystrix.stream")){

            return true;
        }

        return false;
    }

    /**
     * The default value is "false" so that the filter may delay the generation of
     * an ETag until the last asynchronously dispatched thread.
     */
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpServletResponse responseToUse = response;

        //先不考虑异步的情况
        if (!(response instanceof ContentCachingResponseWrapper)) {
            responseToUse = new ContentCachingResponseWrapper(response);
        }

        filterChain.doFilter(request, responseToUse);


        //update response if needed
        updateResponse(responseToUse);
    }

    private void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        Assert.notNull(responseWrapper, "ContentCachingResponseWrapper not found");
        HttpServletResponse rawResponse = (HttpServletResponse) responseWrapper.getResponse();
        int statusCode = responseWrapper.getStatusCode();

        if (rawResponse.isCommitted()) {
            responseWrapper.copyBodyToResponse();
        } else if (isNeedAddMD5(statusCode)) {
            String md5 = this.generateMD5(responseWrapper.getContentInputStream());
            rawResponse.setHeader(HEADER_CHECKSUM, md5);
            responseWrapper.copyBodyToResponse();
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("Response with status code [" + statusCode + "] not eligible for md5");
            }
            responseWrapper.copyBodyToResponse();
        }
    }

    /**
     * 是否需要添加MD5 正常的响应才需要
     */
    protected boolean isNeedAddMD5(int responseStatusCode) {
        if (responseStatusCode >= 200 && responseStatusCode < 300) {
            return true;
        }
        return false;
    }


    /**
     * 生成MD5： md5(salt:md5(body))
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    protected String generateMD5(InputStream inputStream) throws IOException {

        // md5(body)
        StringBuilder builder = new StringBuilder();
        DigestUtils.appendMd5DigestAsHex(inputStream, builder);

        //md5(salt:md5(body))
        StringBuilder md5 = new StringBuilder();
        String now = MD5_SALT + ":" + builder.toString();
        InputStream bodyStream = new ByteArrayInputStream(now.getBytes(Charset.forName("UTF-8")));
        DigestUtils.appendMd5DigestAsHex(bodyStream, md5);
        IOUtils.closeQuietly(bodyStream);

        return md5.toString();
    }


}