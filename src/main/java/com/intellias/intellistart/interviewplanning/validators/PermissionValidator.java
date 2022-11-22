package com.intellias.intellistart.interviewplanning.validators;

import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import java.util.Objects;
import org.springframework.security.core.Authentication;

/**
 * Util class. Checks if authenticated user has same id as specified in request or a COORDINATOR role which means they
 * have permission to perform the request. Otherwise, throws an exception which results in 403 response status code
 */
public class PermissionValidator {

  private PermissionValidator() {
  }

  /**
   * Checks if authenticated user has same id as specified in request or a COORDINATOR role which means they have
   * permission to perform the request. Otherwise, throws an exception which results in 403 response status code
   *
   * @param auth        Spring Security object retrieved from controller
   * @param requestedId user id to compare current user id to
   */
  public static void checkAuthorized(Authentication auth, Long requestedId) {
    if (auth == null || auth.getPrincipal() == null) {
      //should never happen if SecurityFilterChain set properly
      throw new ApplicationErrorException(ErrorCode.INVALID_USER_CREDENTIALS);
    }
    User currentUser = (User) auth.getPrincipal();
    if (currentUser.getRole() != UserRole.COORDINATOR && !Objects.equals(currentUser.getId(), requestedId)) {
      throw new ApplicationErrorException(ErrorCode.ATTEMPT_TO_EDIT_OTHER_USER_DATA);
    }
  }
}
