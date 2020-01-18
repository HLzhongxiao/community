package com.hl.community.dto;

import com.hl.community.model.User;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private String typeName;
    private Integer type;
    private Integer status;
    private Long notifier;
    private Long outerId;
    private String outerTitle;
    private String notifierName;
}
