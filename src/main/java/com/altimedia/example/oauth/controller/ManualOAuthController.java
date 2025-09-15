package com.altimedia.example.oauth.controller;

import com.altimedia.example.oauth.config.auth.SessionUser;
import com.altimedia.example.oauth.domain.Users;
import com.altimedia.example.oauth.service.ManualOAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/manual")
public class ManualOAuthController {
    private final ManualOAuthService manualOAuthService;

    @GetMapping("/")
    public String manualIndex(Model model, HttpSession session) {
        Object user = session.getAttribute("manualUser");
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "manual/index";
    }

    @GetMapping("/login/{provider}")
    public String oauthLogin(@PathVariable String provider) {
        if (provider != null) {
            String authUrl = manualOAuthService.getAuthUrl(provider);
            return "redirect:" + authUrl;
        }
        return "error";
    }

    @GetMapping("/callback/{provider}")
    public String oauthCallback(@PathVariable String provider,
                                @RequestParam String code,
                                HttpSession session) {
        if (provider != null) {
            try {
                Users users = manualOAuthService.processCallback(provider, code);
                session.setAttribute("manualUser", new SessionUser(users));
                return "redirect:/manual/";
            } catch (Exception e) {
                log.error("OAuth callback error", e);
                return "redirect:/manual/?error=oauth_error";
            }
        }

        log.error("OAuth callback error");
        return "redirect:/manual/?error=oauth_error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("manualUser");
        return "redirect:/manual/";
    }
}
