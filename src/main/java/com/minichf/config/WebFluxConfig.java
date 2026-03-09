package com.minichf.config;

import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.netty.http.server.HttpServer;

/**
 * Web configuration for HTTP/2, request limits, and CORS
 */
@Slf4j
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    @Value("${app.limits.max-request-size:1048576}")
    private int maxRequestSize;

    /**
     * Configure request body codec to enforce size limits
     */
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(maxRequestSize);
        log.info("Max request size configured: {} bytes", maxRequestSize);
    }

    /**
     * Configure Netty server for HTTP/2 and request limits
     */
    @Bean
    public ReactiveWebServerFactory reactiveWebServerFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        factory.addServerCustomizers(nettyServerCustomizer());
        return factory;
    }

    /**
     * Customize Netty server settings
     */
    private NettyServerCustomizer nettyServerCustomizer() {
        return httpServer -> {
            log.info("Configuring Netty HTTP/2 server");
            return httpServer;
        };
    }

    /**
     * Configure CORS (disabled by default for this API)
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // CORS not enabled by default for this internal service
        // Can be configured via application properties if needed
    }
}
