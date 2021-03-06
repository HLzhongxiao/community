package com.hl.community.service;

import com.hl.community.dto.CommentDTO;
import com.hl.community.enums.CommentTypeEnum;
import com.hl.community.enums.NotificationStatusEnum;
import com.hl.community.enums.NotificationTypeEnum;
import com.hl.community.exception.CustomErrorCode;
import com.hl.community.exception.CustomizeException;
import com.hl.community.mapper.*;
import com.hl.community.model.Comment;
import com.hl.community.model.Notification;
import com.hl.community.model.Question;
import com.hl.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment, User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomErrorCode.TYPE_PARAM_WRONG);
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            // 回复评论
            Comment dbComment = commentMapper.selectById(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomErrorCode.COMMENT_NOT_FOUND);
            }

            Question question = questionMapper.selectById(dbComment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);
            // 增加评论数
            commentExtMapper.incCommentCount(comment.getParentId(),1);
            // 创建通知
            createNotify(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), question.getId(), NotificationTypeEnum.REPLY_COMMENT);
        }else{
            // 回复问题
            Question question = questionMapper.selectById(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            questionExtMapper.incCommentCount(question.getId(), 1);
            // 创建通知
            createNotify(comment, question.getCreator(), commentator.getName(), question.getTitle(), question.getId(), NotificationTypeEnum.REPLY_QUESTION);
        }
    }

    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, Long outerId, NotificationTypeEnum notificationType) {
        if (receiver == comment.getCommentator()) {
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterId(outerId);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        List<Comment> comments = commentMapper.selectByIdAndType(id, type.getType());
        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        // 获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        // 获取评论人并转换为 Map
        List<User> users = userMapper.selectById(userIds);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        // 转换comment为 commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return  commentDTOS;
    }
}
