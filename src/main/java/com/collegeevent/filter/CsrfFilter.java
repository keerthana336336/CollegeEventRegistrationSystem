package com.collegeevent.filter;

import com.collegeevent.util.CsrfUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Puts the CSRF token on every page and rejects POST requests that do not carry it. */
public class CsrfFilter implements Filter {
    @Override public void init(FilterConfig filterConfig) { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (path.startsWith("/assets/")) {
            chain.doFilter(request, response);
            return;
        }
        if ("POST".equalsIgnoreCase(req.getMethod()) && !CsrfUtil.isValid(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Your form has expired or is invalid. Go back, reload the page and try again.");
            return;
        }
        req.setAttribute(CsrfUtil.SESSION_KEY, CsrfUtil.token(req.getSession(true)));
        chain.doFilter(request, response);
    }

    @Override public void destroy() { }
}
