package com.yphome.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.yphome.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登陆的过滤器
 *
 */

@WebFilter(filterName = "loginCheckFiler", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER  = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        log.info("拦截器请求：{}", httpServletRequest.getRequestURI());
        String requestURI = httpServletRequest.getRequestURI();
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
        boolean check = check(urls, requestURI);
        if(check) {
            log.info("本次请求不需处理{}", requestURI);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        if(httpServletRequest.getSession().getAttribute("employee")!=null){
            log.info("用户已登陆id为{}", httpServletRequest.getSession().getAttribute("employee") );
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        log.info("用户未登录");
        httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match)
                return true;
        }
        return false;
    }
}
