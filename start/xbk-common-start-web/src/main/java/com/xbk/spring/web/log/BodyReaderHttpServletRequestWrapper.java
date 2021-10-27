package com.xbk.spring.web.log;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 用以拷贝请求体
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private String body;

    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        body = getRequestBody(request);
    }

    /**
     * 复制请求体
     */
    private String getRequestBody(HttpServletRequest request) {
        int contentLength = request.getContentLength();
        if (contentLength <= 0) {
            return StringUtils.EMPTY;
        }
        try (BufferedReader reader = request.getReader()) {
            return IOUtils.toString(reader)
                    .replace("\n", "")
                    .replace("\t", "");
        } catch (IOException e) {
            log.error("获取请求体失败", e);
            return StringUtils.EMPTY;
        }
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() {
                return bais.read();
            }
        };
    }
}
