package csa;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import csa.interceptor.AuthorityScopeInterceptor;
import csa.interceptor.MenuInterceptor;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.mediaType("csv", new MediaType("text", "csv"));
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    localeChangeInterceptor.setParamName("lang");

    registry.addInterceptor(localeChangeInterceptor);
    registry.addInterceptor(new AuthorityScopeInterceptor());
    registry.addInterceptor(new MenuInterceptor());
  }
}