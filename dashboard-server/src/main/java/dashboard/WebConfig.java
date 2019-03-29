package dashboard;

import dashboard.util.CookieThenAcceptHeaderLocaleResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import dashboard.control.GsonHttpMessageConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    private static final String LANG = "lang";

    @Value("${dashboard.feature.statistics}")
    private boolean statsEnabled;

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType("csv", new MediaType("text", "csv"));
    }

    @Bean
    public LocaleResolver localeResolver(@Value("${supported_language_codes}") String supportLanguageCodes) {
        String language = Stream.of(supportLanguageCodes.split(",")).map(String::trim).findFirst().orElse("nl");
        CookieThenAcceptHeaderLocaleResolver localeResolver = new CookieThenAcceptHeaderLocaleResolver();
        localeResolver.setCookieName(LANG);
        localeResolver.setDefaultLocale(new Locale(language));
        localeResolver.setCookieMaxAge(315360000);
        return localeResolver;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName(LANG);

        registry.addInterceptor(localeChangeInterceptor);
        registry.addInterceptor(new HandlerInterceptorAdapter() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
                    Exception {
                // add this header as an indication to the JS-client that this is a regular, non-session-expired
                // response.
                response.addHeader("sessionAlive", "success");
                return true;
            }
        });
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new GsonHttpMessageConverter(statsEnabled));
    }
}
