package com.group.totalproject.controller.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebErrorController implements ErrorController {
    @GetMapping({"/", "/error", "/{path:[^\\.]*}"})
    public String index() {
        return "index.html";
    }
}