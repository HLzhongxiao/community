package com.hl.community.model;

import lombok.Data;

@Data
public class Comment {
    private Integer id;
    private Long parentId;
    private Integer type;
    private Integer commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer likeCount;
    private String content;
}
