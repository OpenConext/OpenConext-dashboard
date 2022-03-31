package dashboard.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Controller
public abstract class BaseController {

    @Resource(name = "localeResolver")
    protected LocaleResolver localeResolver;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        String[] denylist = new String[]{"class.*", "Class.*", "*.class.*", "*.Class.*"};
        dataBinder.setDisallowedFields(denylist);
    }

    @ModelAttribute(value = "locale")
    public Locale getLocale(HttpServletRequest request) {
        return localeResolver.resolveLocale(request);
    }

    public <T> RestResponse<T> createRestResponse(T payload) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();

        return RestResponse.of(this.getLocale(request), payload);
    }

}
