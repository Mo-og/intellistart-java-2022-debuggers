package com.intellias.intellistart.interviewplanning;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.services.BookingService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InterviewPlanningApplicationTests {

  @Autowired
  private InterviewerService interviewerService;
  //Booking
  @Autowired
  private BookingService bookingService;
  //UserService
  @Autowired
  private UserService userService;

  @Test
  void interviewerSlotMainScenario() {
    var slot = interviewerService.createSlot(
      "09:00",
      "18:00",
      "WEDNESDAY",
      1);
    assertThat(slot).isNotNull();
  }

  @Test
  @Transactional
  void createBookingTest() {
    assertThat(bookingService.createBooking(new CandidateTimeSlot(), new InterviewerTimeSlot())).isNotNull();
  }

  @Test
  void getBookingTest() {
    assertThat(bookingService.getBooking(1L)).isNotNull();
  }

  @Test
  void addUserTest() {
    assertThat(userService.createUser("abc@gmail.com")).isNotNull();
  }

  @Test
  @Transactional
  void getUserTest() {
    User user = new User("abc@gmail.com", UserRole.CANDIDATE);
    user.setId(userService.saveUser(user).getId());
    User retrievedUser = userService.getUser(user.getId());
    assertThat(retrievedUser).isNotNull();
    assertThat(user.getEmail()).isEqualTo(retrievedUser.getEmail());
    assertThat(user.getUserRole()).isEqualTo(retrievedUser.getUserRole());
    assertThat(user.getId()).isPositive();
  }


}