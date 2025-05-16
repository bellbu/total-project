package com.group.totalproject.controller.error;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Controller
public class WebErrorController implements ErrorController {

    private static final Set<String> reactPaths = Set.of(
            "/", "mainPage", "joinPage", "loginPage", "adminPage", "userPage"
    );

    @GetMapping("/{path:^(?!api|static|favicon\\.ico|.*\\..*).*}")
    public String forwardToIndex(@PathVariable String path) {
        if (reactPaths.contains(path)) {
            return "forward:/index.html";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page not found");
    }

    @GetMapping("/")
    public String root() {
        return "forward:/index.html";
    }

}