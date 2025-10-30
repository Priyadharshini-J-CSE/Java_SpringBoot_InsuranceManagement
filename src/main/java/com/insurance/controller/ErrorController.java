package com.insurance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        
        System.out.println("Error occurred - Status: " + statusCode);
        if (exception != null) {
            System.out.println("Exception: " + exception.getMessage());
            exception.printStackTrace();
        }
        
        model.addAttribute("errorMessage", "Something went wrong. Please try again.");
        return "error";
    }
}