package com.smartwaste.controller;

import com.smartwaste.entity.Complaint;
import com.smartwaste.entity.User;
import com.smartwaste.service.ComplaintService;
import com.smartwaste.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ComplaintService complaintService;

    @Autowired
    public AdminController(UserService userService, ComplaintService complaintService) {
        this.userService = userService;
        this.complaintService = complaintService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        long totalComplaints = complaintService.getAllComplaints().size();
        long pending = complaintService.getCountByStatus("PENDING");
        long assigned = complaintService.getCountByStatus("ASSIGNED") + complaintService.getCountByStatus("IN_PROGRESS");
        long completed = complaintService.getCountByStatus("COMPLETED");
        long workersCount = userService.getAllWorkers().size();

        model.addAttribute("totalComplaints", totalComplaints);
        model.addAttribute("pending", pending);
        model.addAttribute("assigned", assigned);
        model.addAttribute("completed", completed);
        model.addAttribute("workersCount", workersCount);

        List<Complaint> allComplaints = complaintService.getAllComplaints();
        model.addAttribute("recentComplaints", allComplaints.size() > 5 ? allComplaints.subList(0, 5) : allComplaints);

        return "admin/dashboard";
    }

    @GetMapping("/manage-complaints")
    public String manageComplaints(@RequestParam(value = "query", required = false) String query,
                                   @RequestParam(value = "status", required = false) String status,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                   @RequestParam(value = "sort", defaultValue = "date,desc") String sort,
                                   Model model) {
        
        String[] sortParams = sort.split(",");
        org.springframework.data.domain.Sort sortObj = org.springframework.data.domain.Sort.by(
            sortParams[1].equalsIgnoreCase("asc") ? org.springframework.data.domain.Sort.Direction.ASC : org.springframework.data.domain.Sort.Direction.DESC, 
            sortParams[0]
        );
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sortObj);

        org.springframework.data.domain.Page<Complaint> complaintsPage = complaintService.searchAndFilter(query, status, pageable);
        List<User> workers = userService.getAllWorkers();

        model.addAttribute("complaints", complaintsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", complaintsPage.getTotalPages());
        model.addAttribute("totalItems", complaintsPage.getTotalElements());
        model.addAttribute("sort", sort);
        
        model.addAttribute("workers", workers);
        model.addAttribute("query", query);
        model.addAttribute("status", status != null ? status : "ALL");

        return "admin/manage-complaints";
    }

    @PostMapping("/assign-worker")
    public String assignWorker(@RequestParam("complaintId") Long complaintId,
                               @RequestParam("workerId") Long workerId,
                               @RequestParam("priority") String priority) {
        try {
            complaintService.assignWorker(complaintId, workerId, priority);
            return "redirect:/admin/manage-complaints?assigned=true";
        } catch (Exception e) {
            return "redirect:/admin/manage-complaints?error=" + e.getMessage();
        }
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("complaintId") Long complaintId,
                               @RequestParam("status") String status,
                               @RequestParam(value = "remarks", required = false) String remarks) {
        try {
            complaintService.updateStatus(complaintId, status, remarks);
            return "redirect:/admin/manage-complaints?updated=true";
        } catch (Exception e) {
            return "redirect:/admin/manage-complaints?error=" + e.getMessage();
        }
    }

    @GetMapping("/delete-complaint/{id}")
    public String deleteComplaint(@PathVariable("id") Long id) {
        try {
            complaintService.deleteComplaint(id);
            return "redirect:/admin/manage-complaints?deleted=true";
        } catch (Exception e) {
            return "redirect:/admin/manage-complaints?error=" + e.getMessage();
        }
    }

    @GetMapping("/manage-users")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/manage-users";
    }

    @GetMapping("/complaints/{id}")
    public String viewComplaintDetails(@PathVariable("id") Long id, Model model) {
        Complaint complaint = complaintService.getComplaintById(id);
        List<User> workers = userService.getAllWorkers();
        model.addAttribute("complaint", complaint);
        model.addAttribute("workers", workers);
        return "admin/complaint-details";
    }

    @PostMapping("/toggle-block")
    public String toggleBlockStatus(@RequestParam("userId") Long userId) {
        try {
            userService.toggleBlockStatus(userId);
            return "redirect:/admin/manage-users?success=User status updated";
        } catch (Exception e) {
            return "redirect:/admin/manage-users?error=" + e.getMessage();
        }
    }

    @GetMapping("/export-complaints")
    public org.springframework.http.ResponseEntity<byte[]> exportComplaints() {
        List<Complaint> complaints = complaintService.getAllComplaints();
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,Title,Category,Status,Priority,Citizen Name,Area,Date Reported\n");
        for (Complaint c : complaints) {
            csvBuilder.append(String.format("%s,\"%s\",%s,%s,%s,\"%s\",\"%s\",%s\n",
                    c.getComplaintId(),
                    c.getTitle() != null ? c.getTitle().replace("\"", "\"\"") : "",
                    c.getCategory(),
                    c.getStatus(),
                    c.getPriority() != null ? c.getPriority() : "None",
                    c.getCitizen() != null ? c.getCitizen().getName() : "Unknown",
                    c.getArea() != null ? c.getArea().replace("\"", "\"\"") : "",
                    c.getDate() != null ? c.getDate().toString() : ""
            ));
        }
        byte[] csvBytes = csvBuilder.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=complaints_report.csv");
        headers.set(org.springframework.http.HttpHeaders.CONTENT_TYPE, "text/csv");
        return new org.springframework.http.ResponseEntity<>(csvBytes, headers, org.springframework.http.HttpStatus.OK);
    }

    private final java.util.Map<String, String> otpCache = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Set<String> verifiedEmails = java.util.concurrent.ConcurrentHashMap.newKeySet();

    @Autowired
    private com.smartwaste.service.EmailService emailService;

    @PostMapping("/send-otp")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> sendOtp(@RequestParam("email") String email) {
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        otpCache.put(email, otp);
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            emailService.sendVerificationEmail(email, otp);
        });
        return org.springframework.http.ResponseEntity.ok().body("{\"success\":true}");
    }

    @PostMapping("/verify-otp")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> verifyOtp(@RequestParam("email") String email, @RequestParam("otp") String otp) {
        String cachedOtp = otpCache.get(email);
        if (cachedOtp != null && cachedOtp.equals(otp)) {
            verifiedEmails.add(email);
            otpCache.remove(email);
            return org.springframework.http.ResponseEntity.ok().body("{\"success\":true}");
        }
        return org.springframework.http.ResponseEntity.badRequest().body("{\"success\":false}");
    }

    @PostMapping("/register-worker")
    public String registerWorker(@ModelAttribute com.smartwaste.dto.RegisterDto registerDto) {
        try {
            if (!verifiedEmails.contains(registerDto.getEmail())) {
                throw new IllegalArgumentException("Email must be verified before registering worker");
            }
            userService.registerWorker(registerDto);
            verifiedEmails.remove(registerDto.getEmail());
            return "redirect:/admin/manage-users?success=Worker registered successfully";
        } catch (Exception e) {
            return "redirect:/admin/manage-users?error=" + e.getMessage();
        }
    }

    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return "redirect:/admin/manage-users?success=User deleted successfully";
        } catch (Exception e) {
            return "redirect:/admin/manage-users?error=" + e.getMessage();
        }
    }
}
