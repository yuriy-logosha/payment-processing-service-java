package com.company.payment.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class ClientCountryResolveServiceTest {

    @Value( "${client.country.resolver.host}" )
    private String ipResolverHost;

    @Value( "${client.country.resolver.access_key}" )
    private String ipResolverAccessKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ClientCountryResolveService clientCountryResolveService;

    @MockBean
    private ServletRequest servletRequest;

    @MockBean
    private HttpServletRequest httpServletRequest;

    private MockRestServiceServer mockServer;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void describeIP() throws URISyntaxException {
        String ip = "mocked-ip";
        mockServer.expect(once(),
                requestTo(new URI(ipResolverHost + ip + ClientCountryResolveService.ACCESS_KEY + ipResolverAccessKey)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());
        clientCountryResolveService.describeIP(ip);
        mockServer.verify();
    }

    @Test
    public void testDescribeIP() throws URISyntaxException {
        String ip = "testDescribeIP";
        when(servletRequest.getRemoteAddr()).thenReturn(ip);
        mockServer.expect(once(),
                requestTo(new URI(ipResolverHost + ip + ClientCountryResolveService.ACCESS_KEY + ipResolverAccessKey)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());
        clientCountryResolveService.describeIP(servletRequest);
        mockServer.verify();
    }

    @Test
    public void extractIP() {
        final String mockedIP = "test-ip";
        when(httpServletRequest.getRemoteAddr()).thenReturn(mockedIP);
        assertEquals(mockedIP, clientCountryResolveService.extractIP(httpServletRequest));

        when(httpServletRequest.getHeader("X-FORWARDED-FOR")).thenReturn(mockedIP);
        assertEquals(mockedIP, clientCountryResolveService.extractIP(httpServletRequest));
    }
}