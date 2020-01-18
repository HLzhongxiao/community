package com.hl.community.mapper;

import com.hl.community.model.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {
    @Insert("insert into notification(notifier,notifier_name,receiver,outer_title,outerId,type,gmt_create,status) values(#{notifier},#{notifierName},#{receiver},#{outerTitle},#{outerId},#{type},#{gmtCreate},#{status})")
    void insert(Notification notification);

    @Select("select count(1) from notification where receiver=#{id} and status=0")
    Integer countByUserId(@Param("id") Long id);

    @Select("select * from notification where receiver=#{userId} order by gmt_create desc limit #{offset},#{size}")
    List<Notification> listByUserId(@Param("userId") Long userId,@Param("offset") Integer offset,@Param("size") Integer size);

    @Select("select * from notification where id=#{id}")
    Notification selectById(Long id);

    @Update("update notification set status=#{status} where id=#{id}")
    void updateStatusById(@Param("id") Long id,@Param("status") Integer status);
}
