package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.utils.TestUtils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.utils.TestUtils.json;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.intellias.intellistart.interviewplanning.security.jwt.JwtRequestFilter;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.WeekServiceImp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WeekController.class)
@AutoConfigureMockMvc(addFilters = false)
class WeekControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private CoordinatorService coordinatorService;
  @MockBean
  private CommandLineRunner commandLineRunner;
  @MockBean
  private JwtRequestFilter jwtRequestFilter;
  @SpyBean
  private WeekServiceImp weekService;

  @Test
  void testGetCurrentWeek() {
    checkResponseOk(get("/weeks/current"), json(weekService.getCurrentWeekNum()),
        json(new WeekController.WeekNum(weekService.getCurrentWeekNum())), mockMvc);
  }

  @Test
  void testGetNextWeek() {
    checkResponseOk(get("/weeks/next"), json(weekService.getNextWeekNum()),
        json(new WeekController.WeekNum(weekService.getNextWeekNum())), mockMvc);
  }

  @Test
  void testGetDashboardReturnsSomething() {
    //todo update to actually check response json
    checkResponseOk(get("/weeks/{weekId}/dashboard", 202240), null,
        null, mockMvc);
  }


}
