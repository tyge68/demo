package com.adobe.locust.impl;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.client.ProxyHttpClient;
import io.micronaut.http.filter.FilterChain;
import io.micronaut.http.filter.HttpFilter;
import org.reactivestreams.Publisher;

import java.net.MalformedURLException;
import java.net.URL;

@Filter("/**")
public class GlobalFilter implements HttpFilter {

    private static final String[] PROCESSED_PATHS = new String[] {
            "/stop",
            "/swarm",
            "/tasks",
            "/exceptions",
            "/stats/reset",
            "/stats/requests",
            "/swagger"
    };

    private static final URL PROXY_URL;

    static {
        try {
            PROXY_URL = new URL(Application.targets.get(0));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getOrder() {
        return 1000;
    }

    @Override
    public Publisher<? extends HttpResponse<?>> doFilter(HttpRequest<?> request, FilterChain chain) {
        String path = request.getPath();
        for (String filteredPath : PROCESSED_PATHS) {
            if (path.startsWith(filteredPath)) {
                return chain.proceed(request);
            }
        }
        return ProxyHttpClient.create(PROXY_URL).proxy(request);
    }
}
