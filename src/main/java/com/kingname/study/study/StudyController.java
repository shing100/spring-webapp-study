package com.kingname.study.study;

import com.kingname.study.account.CurrentAccount;
import com.kingname.study.domain.Account;
import com.kingname.study.study.form.StudyForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudyController {

    @GetMapping("/new-study")
    public String newStudyForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new StudyForm());
        return "study/form";
    }
}
