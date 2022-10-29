package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.TestUtils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.TestUtils.json;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.WeekService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WeekController.class)
@AutoConfigureMockMvc(addFilters = false)
class WeekControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private CoordinatorService coordinatorService;

  @Test
  void testGetCurrentWeek() {
    checkResponseOk(get("/weeks/current"), json(WeekService.getCurrentWeekNum()),
        json(new WeekController.WeekNum(WeekService.getCurrentWeekNum())), mockMvc);
  }

  @Test
  void testGetNextWeek() {
    checkResponseOk(get("/weeks/next"), json(WeekService.getNextWeekNum()),
        json(new WeekController.WeekNum(WeekService.getNextWeekNum())), mockMvc);
  }

  @Test
  void testGetDashboardReturnsSomething() {
    //todo update to actually check response json
    checkResponseOk(get("/weeks/{weekId}/dashboard", 202240), null,
        null, mockMvc);
  }


}
