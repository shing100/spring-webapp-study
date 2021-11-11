package com.kingname.study.infra.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile({"local", "test"})
@Component
@Slf4j
public class ConsoleEmailService implements EmailService {

    @Override
    public void sendEmail(EmailMessage emailmessage) {
        log.info("sent Email: {}", emailmessage.getMessage());
    }
}
