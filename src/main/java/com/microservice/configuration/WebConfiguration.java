package com.microservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebConfiguration implements WebFluxConfigurer
{
  @Override
  public void addCorsMappings(final CorsRegistry registry)
  {
    registry.addMapping("/**").allowedOrigins("http://localhost:63342");
  }
}
