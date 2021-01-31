package com.kingname.study.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailMessage emailmessage) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailmessage.getTo());
            mimeMessageHelper.setSubject(emailmessage.getSubject());
            mimeMessageHelper.setText(emailmessage.getMessage(), false);
            javaMailSender.send(mimeMessage);
            log.info("sent email: {}", emailmessage.getMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
