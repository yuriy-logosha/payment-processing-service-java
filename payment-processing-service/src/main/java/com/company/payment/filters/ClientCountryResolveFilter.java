package com.company.payment.filters;

import com.company.payment.service.ClientCountryResolveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Component
public class ClientCountryResolveFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(ClientCountryResolveFilter.class);

    private final ClientCountryResolveService ipResolver;

    @Autowired
    public ClientCountryResolveFilter(ClientCountryResolveService ipResolver) {
        this.ipResolver = ipResolver;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            ipResolver.describeIP(request);
        } catch (RuntimeException e) {
            log.error("Error getting information about ip: ", e);
        }

        chain.doFilter(request, response);
    }
}
