package com.intellias.intellistart.interviewplanning.configs.helpers;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@Slf4j
public class AuthenticationHelper {

    public static void attachAccountId(Authentication authentication, String accountId) {
        Object originalDetails = authentication.getDetails();
        if (originalDetails instanceof Details) {
            Details details = (Details) originalDetails;
            details.setAccountId(accountId);
        } else {
            Details details = new Details()
                .setOriginal(originalDetails)
                .setAccountId(accountId);
            ((OAuth2AuthenticationToken) authentication).setDetails(details);
        }
    }

    public static String retrieveAccountId(Authentication authentication) {
        Details details = (Details) authentication.getDetails();
        log.debug("retrieveAccountId: Principal={}.", authentication.getPrincipal());
        return details.getAccountId();
    }

    @Data
    @Accessors(chain = true)
    private static class Details {

        private Object original;
        private String accountId;

    }

}
