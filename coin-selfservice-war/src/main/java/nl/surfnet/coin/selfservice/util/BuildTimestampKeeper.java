package nl.surfnet.coin.selfservice.util;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.ServletContextAware;

/**
 * Puts the build number in the servlet context so that it may be used by anyone that needs it, most
 * notably head.jsp and foot.jsp
 */
public class BuildTimestampKeeper implements ServletContextAware, InitializingBean {

  private ServletContext servletContext;

  @Value("${app.timestamp}")
  private String buildTimestamp;

  @Override
  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    servletContext.setAttribute("buildTimestamp", buildTimestamp);
  }
}
