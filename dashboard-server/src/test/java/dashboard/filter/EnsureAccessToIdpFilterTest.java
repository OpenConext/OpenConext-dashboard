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
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnsureAccessToIdpFilterTest {
    private Manage manage = mock(Manage.class);
    private EnsureAccessToIdpFilter subject = new EnsureAccessToIdpFilter(manage);

    @Test
    public void doFilter() throws IOException, ServletException {
        when(manage.getIdentityProvider(anyString(), anyBoolean())).thenThrow(new IllegalArgumentException());
        MockFilterChain chain = new MockFilterChain();
        subject.doFilter(new MockHttpServletRequest("GET", "/dashboard/api"), new MockHttpServletResponse(), chain);
        assertNotNull(chain.getRequest());
    }
}