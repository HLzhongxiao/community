package com.hl.community.mapper;

import com.hl.community.dto.QuestionDTO;
import com.hl.community.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionMapper {
    @Insert("insert into question(title,description,gmt_create,gmt_modified,creator,comment_count,view_count,like_count,tag) values(#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{commentCount},#{viewCount},#{likeCount},#{tag})")
    public void create(Question question);

    @Select("select * from question order by gmt_create desc limit #{offset},#{size}")
    List<Question> list(@Param("offset") Integer offset, @Param("size") Integer size);

    @Select("select * from question where creator=#{userId} order by gmt_create desc limit #{offset},#{size}")
    List<Question> listByUserId(@Param("userId")Long userId, @Param("offset") Integer offset, @Param("size") Integer size);

    @Select("select count(1) from question")
    Integer count();

    @Select("select count(1) from question where creator=#{userId}")
    Integer countByUserId(@Param("userId") Long userId);

    @Select("select * from question where id=#{id}")
    Question getById(@Param("id") Long id);

    @Update("update question set title=#{title},description=#{description},gmt_modified=#{gmtModified},tag=#{tag},view_count=#{viewCount},like_count=#{likeCount},comment_count=#{commentCount} where id=#{id}")
    void update(Question question);

    @Select("select view_count from question where id = #{id}")
    Integer getViewCount(@Param("id") Long id);

    @Update("update question set view_count=view_count+#{viewCount} where id=#{id}")
    void updateViewCount(@Param("id") Long id, @Param("viewCount")Integer viewCount);

    @Select("select * from question where id=#{id}")
    Question selectById(@Param("id") Long id);
}
