package dashboard.filter;

import dashboard.manage.Manage;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class EnsureAccessToIdpFilterTest {

    private EnsureAccessToIdpFilter subject = new EnsureAccessToIdpFilter(mock(Manage.class));

    @Test
    public void doFilter() throws IOException, ServletException {
        subject.doFilter(new MockHttpServletRequest("GET","/dashboard/api"), new MockHttpServletResponse(), new MockFilterChain());
    }
}