package com.kingname.study.settings;

import com.kingname.study.WithAccount;
import com.kingname.study.account.AccountRepository;
import com.kingname.study.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("shing100")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfile_form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("shing100")
    @DisplayName("프로필 수정 하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account shing100 = accountRepository.findByNickname("shing100");
        assertEquals(bio, shing100.getBio());
    }

    @WithAccount("shing100")
    @DisplayName("프로필 수정 하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "짧은 소개를 수정하는 경우. 짧은 소개를 수정하는 경우. 짧은 소개를 수정하는 경우. 짧은 소개를 수정" +
                "하는 경우.  짧은 소개를 수정하는 경우.  짧은 소개를 수정하는 경우.  짧은 소개를 수정하는 경우. ";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account shing100 = accountRepository.findByNickname("shing100");
        assertNull(shing100.getBio());
    }


}
