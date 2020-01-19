package com.hl.community.mapper;

import com.hl.community.dto.QuestionQueryDTO;
import com.hl.community.model.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface QuestionExtMapper {

    @Update("update question set view_count+=#{viewCount} where id=#{id}")
    int incView(Question record);

    @Update("update question set comment_count=comment_count+#{commentCount} where id=#{id}")
    int incCommentCount(@Param("id") Long id, @Param("commentCount") Integer commentCount);

    @Select("select * from question where tag regexp #{tag} and id!=#{id}")
    List<Question> selectRelated(@Param("tag") String tag, @Param("id") Long id);

    @Select("<script>select count(*) from question <where><if test='search!=null'>and title regexp #{search}</if></where></script>")
    Integer countBySearch(@Param("search") String search);

    @Select("<script>select * from question <where><if test='search!=null'>and title regexp #{search}</if></where> order by gmt_create desc limit #{page},#{size}</script>")
    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);
}