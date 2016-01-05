package selfservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerUtils;
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

  @Bean
  public CacheManager ehCacheCacheManager(@Value("${csa.cache.ehcache.config:classpath:/ehcache.xml}") Resource location) {
    if (location.exists()) {
      return EhCacheManagerUtils.buildCacheManager(location);
    }

    return EhCacheManagerUtils.buildCacheManager();
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
