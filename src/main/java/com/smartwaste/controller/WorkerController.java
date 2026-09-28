package com.smartwaste.controller;

import com.smartwaste.entity.Complaint;
import com.smartwaste.entity.User;
import com.smartwaste.service.ComplaintService;
import com.smartwaste.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/worker")
public class WorkerController {

    private final UserService userService;
    private final ComplaintService complaintService;

    @Autowired
    public WorkerController(UserService userService, ComplaintService complaintService) {
        this.userService = userService;
        this.complaintService = complaintService;
    }

    @GetMapping("/dashboard")
    public String workerDashboard(Principal principal, Model model) {
        String email = principal.getName();
        User worker = userService.findByEmail(email);
        List<Complaint> complaints = complaintService.getWorkerComplaints(email);

        long totalCount = complaints.size();
        long pendingCount = complaints.stream().filter(c -> "ASSIGNED".equals(c.getStatus())).count();
        long progressCount = complaints.stream().filter(c -> "IN_PROGRESS".equals(c.getStatus())).count();
        long completedCount = complaints.stream().filter(c -> "COMPLETED".equals(c.getStatus())).count();

        model.addAttribute("worker", worker);
        model.addAttribute("complaints", complaints);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("progressCount", progressCount);
        model.addAttribute("completedCount", completedCount);

        return "worker/dashboard";
    }

    @GetMapping("/complaints/{id}")
    public String complaintDetails(@PathVariable("id") Long id, Principal principal, Model model) {
        Complaint complaint = complaintService.getComplaintById(id);
        
        // Safety check to ensure worker is authorized to view this complaint
        if (complaint.getWorker() == null || !complaint.getWorker().getEmail().equals(principal.getName())) {
            return "redirect:/worker/dashboard";
        }

        model.addAttribute("complaint", complaint);
        return "worker/assigned-complaints"; // This page handles detailed view and completion
    }

    @PostMapping("/start-work")
    public String startWork(@RequestParam("complaintId") Long complaintId, Principal principal) {
        Complaint complaint = complaintService.getComplaintById(complaintId);
        if (complaint.getWorker() == null || !complaint.getWorker().getEmail().equals(principal.getName())) {
            return "redirect:/worker/dashboard";
        }
        
        complaintService.updateStatus(complaintId, "IN_PROGRESS", "Worker has arrived at location and started cleaning.");
        return "redirect:/worker/dashboard?started=true";
    }

    @PostMapping("/complete")
    public String completeTask(@RequestParam("complaintId") Long complaintId,
                               @RequestParam("afterImage") MultipartFile afterImage,
                               @RequestParam("remarks") String remarks,
                               @RequestParam(value = "workerLat", required = false) Double workerLat,
                               @RequestParam(value = "workerLng", required = false) Double workerLng,
                               Principal principal) {
        Complaint complaint = complaintService.getComplaintById(complaintId);
        if (complaint.getWorker() == null || !complaint.getWorker().getEmail().equals(principal.getName())) {
            return "redirect:/worker/dashboard";
        }

        if (afterImage.isEmpty()) {
            return "redirect:/worker/complaints/" + complaintId + "?error=image_required";
        }

        // Validate location
        if (complaint.getLatitude() != null && complaint.getLongitude() != null) {
            if (workerLat == null || workerLng == null) {
                return "redirect:/worker/complaints/" + complaintId + "?error=Location access is required to verify you are at the site.";
            }
            double distance = calculateDistance(complaint.getLatitude(), complaint.getLongitude(), workerLat, workerLng);
            if (distance > 0.5) { // 500 meters tolerance
                return "redirect:/worker/complaints/" + complaintId + "?error=You are too far from the garbage location to complete this task. (Distance: " + String.format("%.2f", distance) + "km)";
            }
        }

        try {
            complaintService.completeComplaint(complaintId, afterImage, remarks);
            return "redirect:/worker/dashboard?completed=true";
        } catch (Exception e) {
            return "redirect:/worker/complaints/" + complaintId + "?error=" + e.getMessage();
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; 
    }
}
