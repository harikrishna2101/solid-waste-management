package com.smartwaste.controller;

import com.smartwaste.dto.RegisterDto;
import com.smartwaste.entity.User;
import com.smartwaste.service.ComplaintService;
import com.smartwaste.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final UserService userService;
    private final ComplaintService complaintService;

    @Autowired
    public HomeController(UserService userService, ComplaintService complaintService) {
        this.userService = userService;
        this.complaintService = complaintService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Fetch statistics
        long totalComplaints = complaintService.getAllComplaints().size();
        long resolvedComplaints = complaintService.getCountByStatus("COMPLETED");
        long pendingComplaints = complaintService.getCountByStatus("PENDING");
        
        // Count registered citizens
        long registeredCitizens = userService.getAllUsers().stream()
                .filter(user -> "ROLE_CITIZEN".equals(user.getRole()))
                .count();

        model.addAttribute("totalComplaints", totalComplaints);
        model.addAttribute("resolvedComplaints", resolvedComplaints);
        model.addAttribute("pendingComplaints", pendingComplaints);
        model.addAttribute("registeredCitizens", registeredCitizens);

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        // Redirect if already logged in
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String role = auth.getAuthorities().stream()
                    .map(r -> r.getAuthority())
                    .findFirst()
                    .orElse("");
            if ("ROLE_ADMIN".equals(role)) {
                return "redirect:/admin/dashboard";
            } else if ("ROLE_CITIZEN".equals(role)) {
                return "redirect:/user/dashboard";
            } else if ("ROLE_WORKER".equals(role)) {
                return "redirect:/worker/dashboard";
            }
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // Redirect if already logged in
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/";
        }
        model.addAttribute("registerDto", new RegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerDto") RegisterDto registerDto,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.registerCitizen(registerDto);
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/verify")
    public String showVerifyForm(@RequestParam("email") String email, 
                                 @RequestParam(value = "otp", required = false) String otp, 
                                 Model model) {
        // One-click verification if OTP is in URL
        if (otp != null) {
            User user = userService.findByEmail(email);
            if (user != null && !user.isVerified() && otp.trim().equals(user.getVerificationCode())) {
                user.setVerified(true);
                user.setVerificationCode(null);
                userService.saveUser(user);
                return "redirect:/login?verified=true";
            }
            model.addAttribute("errorMessage", "Invalid or Expired Verification Link.");
        }
        model.addAttribute("email", email);
        return "verify";
    }

    @PostMapping("/verify")
    public String verifyOtp(@RequestParam("email") String email, @RequestParam("otp") String otp, Model model) {
        User user = userService.findByEmail(email);
        if (user != null && !user.isVerified() && otp.trim().equals(user.getVerificationCode())) {
            user.setVerified(true);
            user.setVerificationCode(null);
            userService.saveUser(user);
            return "redirect:/login?verified=true";
        }
        model.addAttribute("email", email);
        model.addAttribute("errorMessage", "Invalid Verification Code.");
        return "verify";
    }
}
