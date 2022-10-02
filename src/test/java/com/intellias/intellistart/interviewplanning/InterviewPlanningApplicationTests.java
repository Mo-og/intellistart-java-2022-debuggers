package com.intellias.intellistart.interviewplanning;

import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

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
        "WEDNESDAY");
    assertThat(slot).isNotNull();
  }
}