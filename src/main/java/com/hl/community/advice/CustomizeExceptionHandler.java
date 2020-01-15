package com.hl.community.advice;

import com.alibaba.fastjson.JSON;
import com.hl.community.dto.ResultDTO;
import com.hl.community.exception.CustomErrorCode;
import com.hl.community.exception.CustomizeException;
import com.hl.community.exception.ICustomizeErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)
    Object handle(Throwable e, Model model, HttpServletRequest request, HttpServletResponse response) {

        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {
            ResultDTO resultDTO;
            // 返回JSON
            if (e instanceof CustomizeException) {
                resultDTO = (ResultDTO) ResultDTO.errorOf((CustomizeException) e);
            }else{
                resultDTO = (ResultDTO) ResultDTO.errorOf(CustomErrorCode.SYS_ERROR);
            }

            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            }catch (IOException ioe) {

            }
            return null;
        }else{
            // 返回错误界面
            if (e instanceof CustomizeException) {
                model.addAttribute("message", e.getMessage());
            }else{
                model.addAttribute("message", CustomErrorCode.SYS_ERROR.getMessage());
            }

            return new ModelAndView("error");
        }
    }
}
