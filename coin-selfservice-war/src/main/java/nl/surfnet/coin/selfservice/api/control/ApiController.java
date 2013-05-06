package nl.surfnet.coin.selfservice.api.control;

import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.selfservice.domain.ApiService;
import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.Provider.Language;
import nl.surfnet.coin.selfservice.service.IdentityProviderService;
import nl.surfnet.coin.selfservice.service.LmngService;
import nl.surfnet.coin.selfservice.service.impl.CompoundSPService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.surfnet.oaaas.auth.AuthorizationServerFilter;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.conext.SAMLAuthenticatedPrincipal;
import org.surfnet.oaaas.model.VerifyTokenResponse;

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

  @Resource
  private IdentityProviderService idpService;

  @Value("${public.api.lmng.guids}")
  private String[] guids;

  @RequestMapping(method = RequestMethod.GET,value = "/public/services.json")
  public @ResponseBody
  List<ApiService> getPublicServices(@RequestParam(value = "lang", defaultValue = "en") String language,
      final HttpServletRequest request) {
    if ((Boolean) (request.getAttribute("lmngActive"))) {
      List<CompoundServiceProvider> csPs = compoundSPService.getAllPublicCSPs();
      List<ApiService> result = buildApiServices(csPs, language);

      // add public service from LMNG directly
      for (String guid : guids) {
        Article currentArticle = lmngService.getService(guid);
        ApiService currentPS = new ApiService(currentArticle.getServiceDescriptionNl(), currentArticle.getDetailLogo(),
            null, true, lmngDeepLinkBaseUrl + guid);
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

  @RequestMapping(method = RequestMethod.GET, value = "/protected/services.json")
  public @ResponseBody
  List<ApiService> getProtectedServices(@RequestParam(value = "lang", defaultValue = "en") String language,
      final HttpServletRequest request) {
    if ((Boolean) (request.getAttribute("lmngActive"))) {
      String ipdEntityId = getIdpEntityIdFromToken(request);
      IdentityProvider identityProvider = idpService.getIdentityProvider(ipdEntityId);
      List<CompoundServiceProvider> csPs = compoundSPService.getCSPsByIdp(identityProvider);
      List<CompoundServiceProvider> scopedSsPs = new ArrayList<CompoundServiceProvider>();
      /*
       * We only want the SP's that are currently linked to the IdP, not the also included SP's that are NOT IdP-only
       */
      for (CompoundServiceProvider csp : csPs) {
         if (csp.getServiceProvider().isLinked() && !csp.isHideInProtectedShowroom()) {
           scopedSsPs.add(csp);
         } 
      }
      List<ApiService> result = buildApiServices(scopedSsPs , language);

      sort(result);
      return result;
    } else {
      throw new RuntimeException("Only allowed in showroom, not in dashboard");
    }
  }

  /**
   * Handle CORS preflight request.
   * 
   * @param origin
   *          the Origin header
   * @param methods
   *          the "Access-Control-Request-Method" header
   * @param headers
   *          the "Access-Control-Request-Headers" header
   * @return a ResponseEntity with 204 (no content) and the right response
   *         headers
   */
  @RequestMapping(method = RequestMethod.OPTIONS, value = "/protected/**")
  public ResponseEntity<String> preflightCORS(@RequestHeader("Origin") String origin,
      @RequestHeader(value = "Access-Control-Request-Method", required = false) String[] methods,
      @RequestHeader(value = "Access-Control-Request-Headers", required = false) String[] headers) {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Allow", "GET, OPTIONS, HEAD");
    responseHeaders.set("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD");
    responseHeaders.set("Access-Control-Allow-Headers", "Authorization");
    responseHeaders.set("Access-Control-Max-Age", "86400"); // allow cache of 1
                                                            // day
    return new ResponseEntity<String>(null, responseHeaders, HttpStatus.OK);
  }

  /**
   * Retrieve IDP Entity ID from the oauth token stored in the request
   * 
   * @param request
   *          httpServletRequest to look in.
   * @return identityProvider of the principle
   */
  private String getIdpEntityIdFromToken(final HttpServletRequest request) {
    VerifyTokenResponse verifyTokenResponse = (VerifyTokenResponse) request.getAttribute(AuthorizationServerFilter.VERIFY_TOKEN_RESPONSE);
    AuthenticatedPrincipal authenticatedPrincipal = verifyTokenResponse.getPrincipal();
    if (authenticatedPrincipal instanceof SAMLAuthenticatedPrincipal) {
      SAMLAuthenticatedPrincipal principal = (SAMLAuthenticatedPrincipal) authenticatedPrincipal;
      return principal.getIdentityProvider();
    }
    throw new IllegalArgumentException("Only type of Principal supported is SAMLAuthenticatedPrincipal, not " + authenticatedPrincipal.getClass());
  }

  /**
   * Convert the list of found services to a list of services that can be
   * displayed in the API (either public or private)
   * 
   * @param services
   *          list of services to convert (compound service providers)
   * @param language
   *          language to use in the result
   * @return a list of api services
   */
  private List<ApiService> buildApiServices(List<CompoundServiceProvider> services, String language) {
    List<ApiService> result = new ArrayList<ApiService>();
    boolean isEn = language.equalsIgnoreCase("en");
    for (CompoundServiceProvider csP : services) {
      String crmLink = csP.isArticleAvailable() ? (lmngDeepLinkBaseUrl + csP.getLmngId()) : null;
      result.add(new ApiService(isEn ? csP.getSp().getName(Language.EN) : csP.getSp().getName(Language.NL),
          getServiceLogo(csP), csP.getServiceUrl(), csP.isArticleAvailable(), crmLink));
    }
    return result;
  }

}
