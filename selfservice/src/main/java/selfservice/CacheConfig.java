package selfservice;

import com.google.common.base.Throwables;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import selfservice.cache.ServicesCache;
import selfservice.service.impl.CompoundServiceProviderService;
import selfservice.service.impl.ServicesServiceImpl;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkArgument;

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
  public ServicesCache servicesCache(CompoundServiceProviderService compoundSPService,
                                     @Value("${cache.default.initialDelay}") long initialDelay,
                                     @Value("${cache.default.delay}") long delay,
                                     @Value("${static.baseurl}") String staticBaseUrl) {
    return new ServicesCache(new ServicesServiceImpl(compoundSPService, staticBaseUrl), initialDelay, delay);
  }

}
