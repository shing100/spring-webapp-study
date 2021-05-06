package com.kingname.study.modules.study.event;

import com.kingname.study.modules.study.Study;
import lombok.Getter;

@Getter
public class StudyCreatedEvent {

    private Study study;

    public StudyCreatedEvent(Study study) {
        this.study = study;
    }
}
