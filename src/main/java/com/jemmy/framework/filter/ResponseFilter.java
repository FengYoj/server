package com.jemmy.framework.filter;

import javax.servlet.*;
import java.io.IOException;

//@WebFilter(urlPatterns = "/*", filterName = "ResponseFilter")
public class ResponseFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    }
}
