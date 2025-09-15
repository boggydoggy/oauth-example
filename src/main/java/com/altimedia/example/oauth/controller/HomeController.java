package com.altimedia.example.oauth.controller;

import com.altimedia.example.oauth.config.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
@Slf4j
public class HomeController {
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model) {
        Object user =  httpSession.getAttribute("user");

        log.debug("index");

        if(user != null) {
            model.addAttribute("user", user);
        }

        return "index";
    }
}
