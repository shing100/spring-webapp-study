package com.kingname.study.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.plaf.SpinnerUI;

@Component
@RequiredArgsConstructor
public class AccountFactory {

    @Autowired AccountRepository accountRepository;

    public Account createAccount(String nickname) {
        Account shing100 = new Account();
        shing100.setNickname(nickname);
        shing100.setEmail(nickname + "@email.com");
        accountRepository.save(shing100);
        return shing100;
    }

}
