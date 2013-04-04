package nl.surfnet.coin.selfservice.control.api;

import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.PublicService;
import nl.surfnet.coin.selfservice.service.impl.CompoundSPService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/api/*")
public class ApiController {

  private @Value("${WEB_APPLICATION_CHANNEL}") String protocol;
  private @Value("${WEB_APPLICATION_HOST_AND_PORT}") String hostAndPort;
  private @Value("${WEB_APPLICATION_CONTEXT_PATH}") String contextPath;

  @Resource
  private CompoundSPService compoundSPService;

  @RequestMapping(value = "/public/services.json")
  public
  @ResponseBody
  List<PublicService> getPublicServices(@RequestParam(value = "lang", defaultValue = "en") String language) {
    //made explicit here for tracebility
    List<CompoundServiceProvider> csPs = compoundSPService.getAllPublicCSPs();
    List<PublicService> result = new ArrayList<PublicService>();
    boolean isEn = language.equalsIgnoreCase("en");
    for (CompoundServiceProvider csP : csPs) {
      result.add(new PublicService(isEn ? csP.getServiceDescriptionEn() : csP.getServiceDescriptionNl(),
              getServiceLogo(csP) , csP.getServiceUrl(), csP.isArticleAvailable()));
    }
    return result;
  }

  private String getServiceLogo(CompoundServiceProvider csP) {
    String detailLogo = csP.getDetailLogo();
    if (detailLogo != null) {
      if (detailLogo.startsWith("/")) {
        detailLogo = protocol + "://" +hostAndPort + (StringUtils.hasText(contextPath) ? contextPath : "") + detailLogo;
      }
    }
    return detailLogo;
  }

}
