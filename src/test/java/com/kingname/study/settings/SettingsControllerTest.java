package com.kingname.study.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingname.study.WithAccount;
import com.kingname.study.account.AccountRepository;
import com.kingname.study.account.AccountService;
import com.kingname.study.domain.Account;
import com.kingname.study.domain.Tag;
import com.kingname.study.domain.Zone;
import com.kingname.study.tag.TagForm;
import com.kingname.study.tag.TagRepository;
import com.kingname.study.zone.ZoneForm;
import com.kingname.study.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.kingname.study.settings.SettingsController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;
    @Autowired TagRepository tagRepository;
    @Autowired ZoneRepository zoneRepository;
    @Autowired AccountService accountService;

    private Zone testZone = Zone.builder().city("test").localNameOfCity("테스트시").province("테스트주").build();

    @BeforeEach
    void beforeEach() {
        zoneRepository.save(testZone);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("shing100")
    @DisplayName("계정의 지역 수정폼")
    @Test
    void updateZoneForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ZONES))
                .andExpect(view().name(SETTINGS + ZONES))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithAccount("shing100")
    @DisplayName("계정의 지역 추가")
    @Test
    @Transactional
    void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Account shing100 = accountRepository.findByNickname("shing100");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertTrue(shing100.getZones().contains(zone));
    }

    @WithAccount("shing100")
    @DisplayName("계정의 지역 삭제")
    @Test
    @Transactional
    void removeZone() throws Exception {
        Account shing100 = accountRepository.findByNickname("shing100");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        accountService.addZone(shing100, zone);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(shing100.getZones().contains(zone));
    }

    @WithAccount("shing100")
    @DisplayName("계정의 태그 수정폼 ")
    @Test
    void updateTagForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + TAGS))
                .andExpect(view().name(SETTINGS + TAGS))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("shing100")
    @DisplayName("계정의 태그 추가 ")
    @Test
    @Transactional
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag").get();
        assertNotNull(newTag);
        accountRepository.findByNickname("shing100").getTags().contains(newTag);
    }

    @WithAccount("shing100")
    @DisplayName("계정의 태그 삭제 ")
    @Test
    @Transactional
    void removeTag() throws Exception {
        Account shing100 = accountRepository.findByNickname("shing100");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(shing100, newTag);

        assertTrue(shing100.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(shing100.getTags().contains(newTag));
    }

    @WithAccount("shing100")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfile_form() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + PROFILE))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("shing100")
    @DisplayName("프로필 수정 하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SETTINGS + PROFILE))
                .andExpect(flash().attributeExists("message"));

        Account shing100 = accountRepository.findByNickname("shing100");
        assertEquals(bio, shing100.getBio());
    }

    @WithAccount("shing100")
    @DisplayName("패스워드 수정폼 ")
    @Test
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + PASSWORD))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("shing100")
    @DisplayName("패스워드 수정하기 - 입력값 정상")
    @Test
    void updatePassword() throws Exception {
        String newPassword = "12345678";
        String newPasswordConfirm = "12345678";
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm", newPasswordConfirm)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SETTINGS + PASSWORD))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("shing100");
        assertTrue(passwordEncoder.matches(newPassword, account.getPassword()));
    }

    @WithAccount("shing100")
    @DisplayName("패스워드 수정하기 - 패스워드 불일치")
    @Test
    void updatePasswordFail() throws Exception {
        String newPassword = "12345678";
        String newPasswordConfirm = "123456782";
        mockMvc.perform(post(SettingsController.ROOT + SETTINGS + PASSWORD)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm", newPasswordConfirm)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name( SETTINGS + PASSWORD))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().hasErrors());
    }

}
