package com.posadskiy.skillrepeater.core.exception;

public class SkillNotFoundException extends RuntimeException {

    public SkillNotFoundException() {
        super("Skill not found");
    }
}
