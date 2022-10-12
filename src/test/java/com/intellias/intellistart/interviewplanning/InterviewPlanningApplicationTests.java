package com.intellias.intellistart.interviewplanning;

import static org.assertj.core.api.Assertions.assertThat;

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
  @Transactional
  void createBookingTest() {
    assertThat(bookingService.createBooking(new CandidateTimeSlot(),
        new InterviewerTimeSlot())).isNotNull();
  }

  @Test
  void getBookingTest() {
    assertThat(bookingService.getBooking(1L)).isNotNull();
  }

  //todo check if broken or wrong
/*    @Test
  void addUserTest() {
    assertThat(userService.create("abc@gmail.com")).isNotNull();
  }*/

  @Test
  @Transactional
  void getUserTest() {
    User user = new User("abc@gmail.com", UserRole.CANDIDATE);
    user.setId(userService.save(user).getId());
    User retrievedUser = userService.getById(user.getId());
    assertThat(retrievedUser).isNotNull();
    assertThat(user.getEmail()).isEqualTo(retrievedUser.getEmail());
    assertThat(user.getUserRole()).isEqualTo(retrievedUser.getUserRole());
    assertThat(user.getId()).isPositive();
  }


}