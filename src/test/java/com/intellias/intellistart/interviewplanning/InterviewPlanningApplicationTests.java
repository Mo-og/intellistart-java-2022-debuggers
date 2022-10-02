package com.intellias.intellistart.interviewplanning;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellias.intellistart.interviewplanning.models.TimeSlot;
import com.intellias.intellistart.interviewplanning.services.BookingService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InterviewPlanningApplicationTests {

  @Test
  void contextLoads() {
  }

  @Autowired
  private InterviewerService interviewerService;

  @Test
  void interviewerSlotMainScenario() {
    var slot = interviewerService.createSlot(
        "09:00",
        "18:00",
        "WEDNESDAY",
        1);
    assertThat(slot).isNotNull();
  }

  //Booking
  @Autowired
  private BookingService bookingService;

  @Test
  void createBookingTest() {
    assertThat(bookingService.createBooking(new TimeSlot(), new TimeSlot())).isNotNull();
  }

  @Test
  void getBookingTest() {
    assertThat(bookingService.getBooking(1L)).isNotNull();
  }

  //UserService
  @Autowired
  private UserService userService;

  @Test
  void addUserTest() {
    assertThat(userService.createUser("abc@gmail.com")).isNotNull();
  }

  @Test
  void getUserTest() {
    assertThat(userService.getUser("id")).isNotNull();
  }


}