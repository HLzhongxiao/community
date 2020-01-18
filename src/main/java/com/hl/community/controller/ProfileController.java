package com.hl.community.controller;

import com.hl.community.dto.NotificationDTO;
import com.hl.community.dto.PaginationDTO;
import com.hl.community.mapper.QuestionMapper;
import com.hl.community.mapper.UserMapper;
import com.hl.community.model.User;
import com.hl.community.service.NotificationService;
import com.hl.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          HttpServletRequest request,
                          @RequestParam(name = "page", defaultValue = "1")Integer page,
                          @RequestParam(name = "size", defaultValue = "5")Integer size,
                          Model model) {

        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        if (action.equals("questions")) {
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
            PaginationDTO paginationDTO = questionService.list(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
        }else if (action.equals("replies")){
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");
            PaginationDTO paginationDTO =  notificationService.list(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
        }

        return "profile";
    }
}
