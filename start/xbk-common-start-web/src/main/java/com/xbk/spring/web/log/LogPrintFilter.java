package com.xbk.spring.web.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xbk.core.global.consts.CommonSymbolConst;
import com.xbk.core.global.head.HeadInfo;
import com.xbk.core.global.log.LogCommon;
import com.xbk.core.global.log.LogTraceIdLocal;
import com.xbk.spring.web.config.log.LogConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class LogPrintFilter extends OncePerRequestFilter {

    private final LogConfig logConfig;

    /**
     * 时间格式化
     */
    private static final String JSON_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 实现 日志过滤
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //初始化 traceId
        final String tranceId = LogTraceIdLocal.get();
        if (StringUtils.isBlank(tranceId)) {
            final String headerTraceId = request.getHeader(LogCommon.TRACE_ID);
            if (StringUtils.isNoneBlank(headerTraceId)) {
                LogTraceIdLocal.set(headerTraceId);
            } else {
                LogTraceIdLocal.init();
            }
        }
        MDC.put(LogCommon.TRACE_ID, LogTraceIdLocal.get());
        //处理当前请求日志
        long beginTime = System.currentTimeMillis();
        String requestUrl = request.getRequestURI();
        String contextPath = request.getContextPath();
        String simpleUrl = requestUrl.substring(contextPath.length());
        String requestBody = "";
        String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        if (!requestUrl.contains(CommonSymbolConst.SYMBOL_DOT) && Objects.nonNull(contentType)) {
            //静态请求，不处理
            if (contentType.startsWith(MediaType.APPLICATION_JSON_VALUE) || contentType.startsWith(MediaType.APPLICATION_XML_VALUE)) {
                //JSON xml Rest 请求
                BodyReaderHttpServletRequestWrapper requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
                requestBody = requestWrapper.getBody();
                request = requestWrapper;
            } else if (contentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                //application/x-www-form-urlencoded 表单提交
                requestBody = JSON.toJSONStringWithDateFormat(request.getParameterMap(), JSON_DATE_FORMAT);
            } else if (contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                //multipart/form-data 文件上传
                requestBody = getFormParam(request);
            }
        } else if (Objects.isNull(contentType)) {
            requestBody = JSON.toJSONStringWithDateFormat(request.getParameterMap(), JSON_DATE_FORMAT);
        }
        beforePrintRequest(simpleUrl);
        //输出日志信息
        log.info("requestPath:{},requestHead:{},requestType:{},contentType:{},requestBody:{}",
                simpleUrl, getHeadInfo(request),
                request.getMethod(), contentType, requestBody);
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally {
            final long timeCost = System.currentTimeMillis() - beginTime;
            if (timeCost >= logConfig.getTip95Line()) {
                //上报请求
                beforePrintResponse(simpleUrl, timeCost);
                log.info("requestPath:{},time-cost:{}", simpleUrl, timeCost);
            } else {
                log.info("requestPath:{},time-cost:{}", simpleUrl, timeCost);
            }
            MDC.clear();
            LogTraceIdLocal.clean();
        }
    }

    /**
     * 接入 gelf 处理 - 打印请求接口之前处理/
     */
    private static void beforePrintRequest(String simpleUrl) {

    }

    /**
     * 接入 gelf 处理 - 打印请求时间之后处理
     */
    private static void beforePrintResponse(String simpleUrl, long timeCost) {

    }

    /**
     * 获取请求头信息
     */
    private static String getHeadInfo(HttpServletRequest request) {
        JSONObject headInfo = new JSONObject();
        headInfo.put(HeadInfo.TOKEN, request.getHeader(HeadInfo.TOKEN));
        headInfo.put(HeadInfo.UID, request.getHeader(HeadInfo.UID));
        return headInfo.toJSONString();
    }

    /**
     * 获取表单数据
     */
    private String getFormParam(HttpServletRequest request) {
        MultipartResolver resolver = new StandardServletMultipartResolver();
        MultipartHttpServletRequest mRequest = resolver.resolveMultipart(request);

        Map<String, Object> param = new HashMap<>();
        Map<String, String[]> parameterMap = mRequest.getParameterMap();
        if (!parameterMap.isEmpty()) {
            param.putAll(parameterMap);
        }
        Map<String, MultipartFile> fileMap = mRequest.getFileMap();
        if (!fileMap.isEmpty()) {
            for (Map.Entry<String, MultipartFile> fileEntry : fileMap.entrySet()) {
                MultipartFile file = fileEntry.getValue();
                param.put(fileEntry.getKey(), file.getOriginalFilename() + "(" + file.getSize() + " byte)");
            }
        }
        return JSON.toJSONStringWithDateFormat(parameterMap, JSON_DATE_FORMAT);
    }


}


