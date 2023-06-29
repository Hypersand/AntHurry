package com.ant.hurry.boundedContext.member.exceptionHandler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class FileUploadExceptionAdvice {

    @Value("spring.servlet.multipart.maxFileSize")
    private String fileSize;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exc) {
        ModelAndView modelAndView = new ModelAndView("error/fileUpload_exception");
        modelAndView.getModel().put("message", "업로드 파일의 용량은 10MB 이하로 올려주세요.");
        return modelAndView;
    }
}