package com.collegeevent.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** UTF-8 everywhere, the "ctx" (context path) request attribute for JSPs, and basic security headers. */
public class EncodingFilter implements Filter {
    @Override public void init(FilterConfig filterConfig) { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        req.setAttribute("ctx", req.getContextPath());

        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "SAMEORIGIN");
        resp.setHeader("Referrer-Policy", "same-origin");

        String path = req.getRequestURI().substring(req.getContextPath().length());
        boolean dynamicPage = !path.startsWith("/assets/");
        if (dynamicPage) { // keep logged-in pages out of the browser cache so Back does not show them after logout
            resp.setHeader("Cache-Control", "no-store");
            resp.setHeader("Pragma", "no-cache");
        }
        chain.doFilter(request, response);
    }

    @Override public void destroy() { }
}
