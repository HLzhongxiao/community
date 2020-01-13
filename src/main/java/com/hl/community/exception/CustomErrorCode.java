package com.hl.community.exception;

public enum CustomErrorCode implements ICustomizeErrorCode {

    QUESTION_NOT_FOUND("你找到问题不在了，要不要换个试试？");

    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    CustomErrorCode(String message) {
        this.message = message;
    }
}
