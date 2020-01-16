package com.hl.community.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CommentExtMapper {

    @Update("update comment set comment_count=comment_count+#{commentCount} where id=#{id}")
    int incCommentCount(@Param("id") Long id, @Param("commentCount") Integer commentCount);
}
