package com.smartwaste.controller;

import com.smartwaste.dto.ComplaintDto;
import com.smartwaste.dto.ProfileDto;
import com.smartwaste.entity.Complaint;
import com.smartwaste.entity.User;
import com.smartwaste.service.ComplaintService;
import com.smartwaste.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class CitizenController {

    private final UserService userService;
    private final ComplaintService complaintService;

    @Autowired
    public CitizenController(UserService userService, ComplaintService complaintService) {
        this.userService = userService;
        this.complaintService = complaintService;
    }

    @GetMapping("/user/dashboard")
    public String citizenDashboard(Principal principal, Model model) {
        String email = principal.getName();
        User citizen = userService.findByEmail(email);
        List<Complaint> complaints = complaintService.getCitizenComplaints(email);

        long totalCount = complaints.size();
        long pendingCount = complaints.stream().filter(c -> "PENDING".equals(c.getStatus())).count();
        long assignedCount = complaints.stream().filter(c -> "ASSIGNED".equals(c.getStatus()) || "IN_PROGRESS".equals(c.getStatus())).count();
        long completedCount = complaints.stream().filter(c -> "COMPLETED".equals(c.getStatus())).count();

        model.addAttribute("citizen", citizen);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("assignedCount", assignedCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("recentComplaints", complaints.size() > 5 ? complaints.subList(0, 5) : complaints);

        return "user/dashboard";
    }

    @GetMapping("/user/raise-complaint")
    public String showRaiseComplaintForm(Principal principal, Model model) {
        User citizen = userService.findByEmail(principal.getName());
        ComplaintDto complaintDto = new ComplaintDto();
        // complaintDto.setPhone(citizen.getPhone()); // Removed prefill phone
        model.addAttribute("complaintDto", complaintDto);
        return "user/raise-complaint";
    }

    @PostMapping("/user/raise-complaint")
    public String raiseComplaint(@Valid @ModelAttribute("complaintDto") ComplaintDto complaintDto,
                                 BindingResult result, Principal principal, Model model) {
        if (complaintDto.getImageFile() == null || complaintDto.getImageFile().isEmpty()) {
            result.rejectValue("imageFile", "error.complaintDto", "Garbage image is required");
        }

        if (result.hasErrors()) {
            return "user/raise-complaint";
        }

        try {
            complaintService.raiseComplaint(complaintDto, principal.getName());
            return "redirect:/user/my-complaints?success=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error submitting complaint: " + e.getMessage());
            return "user/raise-complaint";
        }
    }

    @GetMapping("/user/my-complaints")
    public String myComplaints(Principal principal, Model model) {
        List<Complaint> complaints = complaintService.getCitizenComplaints(principal.getName());
        model.addAttribute("complaints", complaints);
        return "user/my-complaints";
    }

    @GetMapping("/user/complaints/{id}")
    public String complaintDetails(@PathVariable("id") Long id, Principal principal, Model model) {
        Complaint complaint = complaintService.getComplaintById(id);
        
        // Safety check to ensure citizens can only view their own complaints
        if (!complaint.getCitizen().getEmail().equals(principal.getName())) {
            return "redirect:/user/dashboard";
        }

        model.addAttribute("complaint", complaint);
        return "user/complaint-details";
    }

    @GetMapping("/user/profile")
    public String showProfile(Principal principal, Model model) {
        User citizen = userService.findByEmail(principal.getName());
        ProfileDto profileDto = new ProfileDto();
        profileDto.setName(citizen.getName());
        profileDto.setPhone(citizen.getPhone());
        profileDto.setAddress(citizen.getAddress());

        model.addAttribute("profileDto", profileDto);
        model.addAttribute("user", citizen);
        return "user/profile";
    }

    @PostMapping("/user/profile")
    public String updateProfile(@Valid @ModelAttribute("profileDto") ProfileDto profileDto,
                                BindingResult result, Principal principal, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", userService.findByEmail(principal.getName()));
            return "user/profile";
        }

        try {
            userService.updateProfile(principal.getName(), profileDto);
            return "redirect:/user/profile?success=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", userService.findByEmail(principal.getName()));
            return "user/profile";
        }
    }
}
