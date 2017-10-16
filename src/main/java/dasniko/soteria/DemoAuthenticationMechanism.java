package dasniko.soteria;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.AutoApplySession;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.authentication.mechanism.http.RememberMe;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@AutoApplySession
@RememberMe(
    cookieMaxAgeSeconds = 60 * 60 * 24 * 14, // 14 days
    cookieSecureOnly = false, // this is just for demo and local purposes, don't use this in production!!
    isRememberMeExpression = "#{self.isRememberMe(httpMessageContext)}"
)
@LoginToContinue(
    loginPage = "/soteria/login",
    useForwardToLogin = false
)
@ApplicationScoped
public class DemoAuthenticationMechanism implements HttpAuthenticationMechanism {

    @Inject
    private IdentityStore identityStore;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response,
                                                HttpMessageContext httpMessageContext) throws AuthenticationException {
        Credential credential = httpMessageContext.getAuthParameters().getCredential();

        if (credential != null) {
            return httpMessageContext.notifyContainerAboutLogin(identityStore.validate(credential));
        } else {
            return httpMessageContext.doNothing();
        }
    }

    public Boolean isRememberMe(HttpMessageContext httpMessageContext) {
        return httpMessageContext.getAuthParameters().isRememberMe();
    }

    @Override
    public void cleanSubject(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) {
        HttpAuthenticationMechanism.super.cleanSubject(request, response, httpMessageContext);
    }

}
