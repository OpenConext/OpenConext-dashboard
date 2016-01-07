package selfservice;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.base.Throwables;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import net.sf.ehcache.CacheManager;
import selfservice.cache.ServicesCache;
import selfservice.service.CrmService;
import selfservice.service.impl.CompoundSPService;
import selfservice.service.impl.ServicesServiceImpl;

@EnableCaching
@Configuration
public class CacheConfig {

  @Bean
  public EhCacheCacheManager cacheManager(CacheManager ehCacheCacheManager) {
    return new EhCacheCacheManager(ehCacheCacheManager);
  }

  @Bean(destroyMethod = "shutdown")
  public CacheManager ehCacheCacheManager(@Value("${csa.cache.ehcache.config:classpath:/ehcache.xml}") Resource location) {
    checkArgument(location.exists());
    try (InputStream is = location.getInputStream()) {
      return CacheManager.create(is);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  @Bean
  public ServicesCache servicesCache(CompoundSPService compoundSPService, CrmService crmService,
                                     @Value("${cache.default.initialDelay}") long initialDelay,
                                     @Value("${cache.default.delay}") long delay,
                                     @Value("${cacheMillisecondsCallDelay}") long callDelay,
                                     @Value("${static.baseurl}") String staticBaseUrl,
                                     @Value("${lmngDeepLinkBaseUrl}") String lmngDeepLinkBaseUrl,
                                     @Value("${public.api.lmng.guids}") String[] guids) {
    return new ServicesCache(new ServicesServiceImpl(compoundSPService, crmService, staticBaseUrl, lmngDeepLinkBaseUrl, guids), initialDelay, delay, callDelay);
  }

}
