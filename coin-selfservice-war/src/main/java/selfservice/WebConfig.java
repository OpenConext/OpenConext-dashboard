package selfservice;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import selfservice.api.rest.GsonHttpMessageConverter;
import selfservice.interceptor.AuthorityScopeInterceptor;
import selfservice.interceptor.MenuInterceptor;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

  @Value("${statsBaseUrl}")
  private String statsBaseUrl;
  @Value("${statsClientId}")
  private  String statsClientId;
  @Value("${statsScope}")
  private String statsScope;
  @Value("${statsRedirectUri}")
  private String statsRedirectUri;

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
    registry.addInterceptor(new HandlerInterceptorAdapter() {
      @Override
      public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // add this header as an indication to the JS-client that this is a regular, non-session-expired response.
        response.addHeader("sessionAlive", "success");
        return true;
      }
    });
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new GsonHttpMessageConverter(statsBaseUrl, statsClientId, statsScope, statsRedirectUri));
  }
}
