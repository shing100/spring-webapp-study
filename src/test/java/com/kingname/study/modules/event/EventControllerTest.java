package com.kingname.study.modules.event;

import com.kingname.study.modules.account.WithAccount;
import com.kingname.study.modules.account.Account;
import com.kingname.study.modules.study.Study;
import com.kingname.study.modules.study.StudyControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerTest extends StudyControllerTest {

    @Autowired EventService eventService;
    @Autowired EnrollmentRepository enrollmentRepository;

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
    @WithAccount("shing100")
    void newEnrollment_to_FCFS_event_accepted() throws Exception {
        Account shing100 = createAccount("shing100");
        Study study = createStudy("test-study", shing100);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, shing100);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account name = accountRepository.findByNickname("shing100");
        isAccepted(name, event);
    }

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 대기중 (이미 인원이 꽉차서)")
    @WithAccount("shing100")
    void newEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account shing100 = createAccount("shing100");
        Study study = createStudy("test-study", shing100);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, shing100);

        Account may = createAccount("may");
        Account june = createAccount("june");
        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, june);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account account = accountRepository.findByNickname("shing100");
        isNotAccepted(account, event);
    }

    @Test
    @DisplayName("참가신청 확정자가 선착순 모임에 참가 신청을 취소하는 경우, 바로 다음 대기자를 자동으로 신청 확인한다.")
    @WithAccount("shing100")
    void accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account nickname = accountRepository.findByNickname("shing100");
        Account shing100 = createAccount("shing100");
        Account may = createAccount("may");
        Study study = createStudy("test-study", shing100);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, shing100);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, nickname);
        eventService.newEnrollment(event, shing100);

        isAccepted(may, event);
        isAccepted(nickname, event);
        isNotAccepted(shing100, event);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(shing100, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, nickname));
    }

    @Test
    @DisplayName("참가신청 비확정자가 선착순 모임에 참가 신청을 취소하는 경우, 기존 확정자를 그대로 유지하고 새로운 확정자는 없다.")
    @WithAccount("shing100")
    void not_accepterd_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account nickname = accountRepository.findByNickname("shing100");
        Account shing100 = createAccount("shing100");
        Account may = createAccount("may");
        Study study = createStudy("test-study", shing100);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, shing100);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, shing100);
        eventService.newEnrollment(event, nickname);

        isAccepted(may, event);
        isAccepted(shing100, event);
        isNotAccepted(nickname, event);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(shing100, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, nickname));
    }

    private void isNotAccepted(Account whiteship, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, whiteship).isAccepted());
    }

    private void isAccepted(Account account, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    @Test
    @DisplayName("관리자 확인 모임에 참가 신청 - 대기중")
    @WithAccount("shing100")
    void newEnrollment_to_CONFIMATIVE_event_not_accepted() throws Exception {
        Account shing100 = createAccount("shing100");
        Study study = createStudy("test-study", shing100);
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, study, shing100);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account keesun = accountRepository.findByNickname("shing100");
        isNotAccepted(keesun, event);
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Study study, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreateDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        return eventService.createEvent(event, study, account);
    }
}
