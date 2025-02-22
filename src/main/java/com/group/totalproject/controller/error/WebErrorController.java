package com.group.totalproject.controller.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebErrorController implements ErrorController {
    @GetMapping({"/", "/error", "/loginPage", "/joinPage", "/mainPage", "userPage", "adminPage"})
    public String index() {
        return "forward:/index.html";
    }
}