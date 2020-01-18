package com.hl.community.service;

import com.hl.community.dto.NotificationDTO;
import com.hl.community.dto.PaginationDTO;
import com.hl.community.enums.NotificationStatusEnum;
import com.hl.community.enums.NotificationTypeEnum;
import com.hl.community.exception.CustomErrorCode;
import com.hl.community.exception.CustomizeException;
import com.hl.community.mapper.NotificationMapper;
import com.hl.community.mapper.UserMapper;
import com.hl.community.model.Notification;
import com.hl.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        Integer totalCount = notificationMapper.countByUserId(userId);
        Integer totalPage = 0;
        if (totalCount % size == 0){
            totalPage = totalCount / size;
        }else{
            totalPage = totalCount / size + 1;
        }
        if (page < 1 || page > totalPage){
            page = 1;
        }

        Integer offset = size * (page - 1);

        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalPage(totalPage);

        List<Notification> notifications = notificationMapper.listByUserId(userId, offset, size);

        if (notifications.size() == 0) {
            return paginationDTO;
        }

        Set<Long> disUserIds = notifications.stream().map(notify -> notify.getNotifier()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>(disUserIds);
        List<User> users = userMapper.selectById(userIds);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(u->u.getId(), u->u));
        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            // 将question中的所有属性拷贝到questionDTO中去
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }

        paginationDTO.setData(notificationDTOS);
        paginationDTO.setPagination(totalCount, page, size);
        return paginationDTO;
    }

    public Integer unreadCount(Long id) {
        return notificationMapper.countByUserId(id);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new CustomizeException(CustomErrorCode.NOTIFICATION_NOT_FOUND);
        }else if (!Objects.equals(notification.getReceiver(), user.getId())) {
            throw new CustomizeException(CustomErrorCode.READ_NOTIFICATION_FAIL);
        }
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateStatusById(id, notification.getStatus());

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
