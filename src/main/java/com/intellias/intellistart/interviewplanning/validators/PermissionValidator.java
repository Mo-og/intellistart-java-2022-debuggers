package com.intellias.intellistart.interviewplanning.validators;

import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import java.util.Objects;
import org.springframework.security.core.Authentication;

/**
 * Util class. Checks if authenticated user has same id as specified in request or a COORDINATOR
 * role which means they have permission to perform the request. Otherwise, throws an exception
 * which results in 403 response status code
 */
public class PermissionValidator {

  private PermissionValidator() {
  }

  /**
   * Checks if authenticated user has same id as specified in request or a COORDINATOR role which
   * means they have permission to perform the request.
   *
   * @param auth        Spring Security object retrieved from controller
   * @param requestedId user id to compare current user id to
   * @throws ApplicationErrorException if a user tries to change non-his data
   */
  public static void checkAuthorized(Authentication auth, Long requestedId) {
    checkCredentials(auth);
    User currentUser = (User) auth.getPrincipal();
    if (currentUser.getRole() != UserRole.COORDINATOR && !Objects.equals(currentUser.getId(),
        requestedId)) {
      throw new ApplicationErrorException(ErrorCode.ATTEMPT_TO_EDIT_OTHER_USER_DATA);
    }
  }

  /**
   * Provides the user email depending on whether the coordinator is authorized or other user.
   *
   * @param auth  Spring Security object retrieved from controller
   * @param email provided email by coordinator
   * @return user email
   * @throws ApplicationErrorException if the coordinator did not provide the user email
   */
  public static String getUserEmail(Authentication auth, String email) {
    checkCredentials(auth);
    User currentUser = (User) auth.getPrincipal();
    if (currentUser.getRole() == UserRole.COORDINATOR) {
      if (email == null || email.isBlank()) {
        throw new ApplicationErrorException(ErrorCode.NO_EMAIL_SPECIFIED);
      } else {
        return email;
      }
    }
    return currentUser.getEmail();
  }

  /**
   * Checks if user is authenticated.
   *
   * @param auth Spring Security object retrieved from controller
   * @throws ApplicationErrorException if user credentials are invalid
   */
  private static void checkCredentials(Authentication auth) {
    if (auth == null || auth.getPrincipal() == null) {
      //should never happen if SecurityFilterChain set properly
      throw new ApplicationErrorException(ErrorCode.INVALID_USER_CREDENTIALS);
    }
  }
}
