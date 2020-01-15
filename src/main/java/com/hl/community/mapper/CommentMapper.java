package com.hl.community.mapper;

import com.hl.community.model.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Insert("insert into comment(parent_id,type,commentator,gmt_create,gmt_modified,like_count,content) values(#{parentId},#{type},#{commentator},#{gmtCreate},#{gmtModified},#{likeCount},#{content})")
    public void insert(Comment comment);

    @Select("select * from comment where id=#{id}")
    Comment selectById(@Param("id") Long id);

    @Select("select * from comment where parent_id=#{id} and type=#{type} order by gmt_create desc")
    List<Comment> selectByIdAndType(@Param("id") Long id, @Param("type") Integer type);
}
