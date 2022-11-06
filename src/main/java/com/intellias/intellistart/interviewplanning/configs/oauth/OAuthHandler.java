package com.intellias.intellistart.interviewplanning.configs.oauth;

import com.intellias.intellistart.interviewplanning.configs.helpers.AuthenticationHelper;
import com.intellias.intellistart.interviewplanning.configs.helpers.CookieHelper;
import com.intellias.intellistart.interviewplanning.exceptions.UserNotFoundException;
import com.intellias.intellistart.interviewplanning.services.UserService;
import java.time.Duration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class OAuthHandler {

    /**
     * Default =
     * {@value OAuth2AuthorizationRequestRedirectFilter#DEFAULT_AUTHORIZATION_REQUEST_BASE_URI}
     * <p>
     * For instance: - /oauth2/authorization/auth0 - /oauth2/authorization/facebook -
     * /oauth2/authorization/google
     */
    public static final String AUTHORIZATION_BASE_URL = "/oauth2/authorization";

    /**
     * Default = {@value OAuth2LoginAuthenticationFilter#DEFAULT_FILTER_PROCESSES_URI}
     * <p>
     * For instance: - /oauth2/callback/auth0 - /oauth2/callback/facebook - /oauth2/callback/google
     */
    public static final String CALLBACK_BASE_URL = "/oauth2/callback";

    public static final String OAUTH_COOKIE_NAME = "OAUTH";
    public static final String SESSION_COOKIE_NAME = "SESSION";

    private final UserService accountService;

    @SneakyThrows
    public void oauthRedirectResponse(HttpServletRequest request, HttpServletResponse response,
        String url) {
        log.debug("oauthRedirectResponse");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(String.format("{ \"redirectUrl\": \"%s\" }", url));
    }

    @SneakyThrows
    public void oauthSuccessCallback(OAuth2AuthorizedClient client, Authentication authentication) {
        // You can grab the access + refresh tokens as well via the "client"
        Long accountId;
        try {
            log.debug("Trying to resolve email. Principal: {}", authentication.getPrincipal());
            accountId = this.accountService.getByEmail(
                ((DefaultOAuth2User) authentication.getPrincipal()).getAttribute("email")).getId();
            log.debug("Email found: {}", accountId);
        } catch (UserNotFoundException e) {
            accountId = 0L;
        }
        AuthenticationHelper.attachAccountId(authentication, accountId.toString());
    }

    @SneakyThrows
    public void oauthSuccessResponse(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        log.debug("Auth success. Authentication: {}", response, authentication.toString());
        String accountId = AuthenticationHelper.retrieveAccountId(authentication);
        response.addHeader(HttpHeaders.SET_COOKIE,
            CookieHelper.generateExpiredCookie(OAUTH_COOKIE_NAME));
        response.addHeader(HttpHeaders.SET_COOKIE,
            CookieHelper.generateCookie(SESSION_COOKIE_NAME, accountId, Duration.ofDays(1)));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{ \"status\": \"success\" }");
    }

    @SneakyThrows
    public void oauthFailureResponse(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) {
        log.debug("oauthFailureResponse");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.SET_COOKIE,
            CookieHelper.generateExpiredCookie(OAUTH_COOKIE_NAME));
        response.getWriter().write("{ \"status\": \"failure\" }");
    }

}
