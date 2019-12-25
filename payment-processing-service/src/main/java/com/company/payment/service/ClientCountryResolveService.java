package com.company.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

@Service
public class ClientCountryResolveService {
    private static final Logger log = LoggerFactory.getLogger(ClientCountryResolveService.class);

    public final static String ACCESS_KEY = "?access_key=";

    private final RestTemplate restTemplate;

    @Value( "${client.country.resolver.host}" )
    private String ipResolverHost;

    @Value( "${client.country.resolver.access_key}" )
    private String ipResolverAccessKey;

    @Autowired
    public ClientCountryResolveService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void describeIP(ServletRequest request) {
        describeIP(extractIP(request));
    }

    public void describeIP(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return;
        }
        final String result = restTemplate.getForObject(ipResolverHost + ip + ACCESS_KEY + ipResolverAccessKey, String.class);
        log.info(result);
    }

    public String extractIP(ServletRequest request) {
        if(request instanceof HttpServletRequest) {
            final HttpServletRequest httpRequest = (HttpServletRequest) request;
            final String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
            if (!StringUtils.isEmpty(ipAddress)) {
                return ipAddress;
            }
        }

        return request.getRemoteAddr();
    }
}
