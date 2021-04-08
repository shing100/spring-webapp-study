package com.kingname.study.event;

import com.kingname.study.domain.Account;
import com.kingname.study.domain.Enrollment;
import com.kingname.study.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);
}
