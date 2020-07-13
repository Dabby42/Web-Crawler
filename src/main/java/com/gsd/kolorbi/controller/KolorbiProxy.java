package com.gsd.kolorbi.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

@Controller
@RequestMapping(value = "/mirror")
public class KolorbiProxy {

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity mirrorRest(@RequestBody(required = false) String body,
                                     HttpServletRequest request,
                                     @RequestParam(value = "url", required = true) String url)
            throws URISyntaxException {

        URI uri = new URI(url);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if(headerName.equalsIgnoreCase("host") || headerName.equalsIgnoreCase("cookie") || headerName.equalsIgnoreCase("referer")){
                continue;
            }
            headers.set(headerName, request.getHeader(headerName));
        }

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);


        try {
            ResponseEntity response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity,  byte[].class);
            return response;
        } catch(HttpStatusCodeException e) {
            e.printStackTrace();
            return ResponseEntity.status(e.getRawStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        }
    }

}
