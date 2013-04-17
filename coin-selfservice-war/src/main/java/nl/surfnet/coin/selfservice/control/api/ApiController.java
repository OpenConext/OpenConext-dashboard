package nl.surfnet.coin.selfservice.control.api;

import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.PublicService;
import nl.surfnet.coin.selfservice.service.LmngService;
import nl.surfnet.coin.selfservice.service.impl.CompoundSPService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api/*")
public class ApiController {

  private @Value("${WEB_APPLICATION_CHANNEL}")
  String protocol;
  private @Value("${WEB_APPLICATION_HOST_AND_PORT}")
  String hostAndPort;
  private @Value("${WEB_APPLICATION_CONTEXT_PATH}")
  String contextPath;
  private @Value("${lmngDeepLinkBaseUrl}")
  String lmngDeepLinkBaseUrl;

  @Resource
  private CompoundSPService compoundSPService;
  
  @Resource
  private LmngService lmngService;
  
  @Value("${public.api.lmng.guids}")
  private String[] guids;

  @RequestMapping(value = "/public/services.json")
  public @ResponseBody
  List<PublicService> getPublicServices(@RequestParam(value = "lang", defaultValue = "en") String language,
      final HttpServletRequest request) {
    if ((Boolean) (request.getAttribute("lmngActive"))) {
      // made explicit here for tracebility
      List<CompoundServiceProvider> csPs = compoundSPService.getAllPublicCSPs();
      List<PublicService> result = new ArrayList<PublicService>();
      boolean isEn = language.equalsIgnoreCase("en");
      for (CompoundServiceProvider csP : csPs) {
        String crmLink = csP.isArticleAvailable() ? (lmngDeepLinkBaseUrl + csP.getLmngId()) : null;
        result.add(new PublicService(isEn ? csP.getServiceDescriptionEn() : csP.getServiceDescriptionNl(),
            getServiceLogo(csP), csP.getServiceUrl(), csP.isArticleAvailable(), crmLink));
      }
      
      //add public service from LMNG directly
      for (String guid : guids) {
        Article currentArticle = lmngService.getService(guid);
        PublicService currentPS = new PublicService(currentArticle.getServiceDescriptionNl(), currentArticle.getDetailLogo(), null, true, lmngDeepLinkBaseUrl + guid);
        result.add(currentPS);
      }
      sort(result);
      return result;
    } else {
      throw new RuntimeException("Only allowed in showroom, not in dashboard");
    }
  }

  private String getServiceLogo(CompoundServiceProvider csP) {
    String detailLogo = csP.getDetailLogo();
    if (detailLogo != null) {
      if (detailLogo.startsWith("/")) {
        detailLogo = protocol + "://" + hostAndPort + (StringUtils.hasText(contextPath) ? contextPath : "")
            + detailLogo;
      }
    }
    return detailLogo;
  }
}
