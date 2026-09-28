package com.smartwaste.service;

import com.smartwaste.dto.ComplaintDto;
import com.smartwaste.entity.Complaint;
import com.smartwaste.entity.User;
import com.smartwaste.repository.ComplaintRepository;
import com.smartwaste.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public ComplaintServiceImpl(ComplaintRepository complaintRepository, UserRepository userRepository, EmailService emailService, CloudinaryService cloudinaryService) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public Complaint raiseComplaint(ComplaintDto complaintDto, String citizenEmail) {
        User citizen = userRepository.findByEmail(citizenEmail)
                .orElseThrow(() -> new IllegalArgumentException("Citizen not found with email: " + citizenEmail));

        String generatedId = "COMP-" + (100000 + new Random().nextInt(900000));

        String savedFilename = null;
        if (complaintDto.getImageFile() != null && !complaintDto.getImageFile().isEmpty()) {
            try {
                savedFilename = cloudinaryService.uploadFile(complaintDto.getImageFile());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload garbage image to Cloudinary", e);
            }
        }

        Complaint complaint = Complaint.builder()
                .complaintId(generatedId)
                .title(complaintDto.getTitle())
                .category(complaintDto.getCategory())
                .description(complaintDto.getDescription())
                .area(complaintDto.getArea())
                .landmark(complaintDto.getLandmark())
                .phone(complaintDto.getPhone())
                .latitude(complaintDto.getLatitude())
                .longitude(complaintDto.getLongitude())
                .imagePath(savedFilename)
                .status("PENDING")
                .citizen(citizen)
                .build();

        Complaint savedComplaint = complaintRepository.save(complaint);

        // Find Admin email
        List<User> admins = userRepository.findByRole("ROLE_ADMIN");
        String adminEmail = admins.isEmpty() ? null : admins.get(0).getEmail();

        // Send Email asynchronously
        CompletableFuture.runAsync(() -> {
            emailService.sendComplaintRegistrationEmail(savedComplaint, citizenEmail, adminEmail);
        });

        return savedComplaint;
    }

    @Override
    public List<Complaint> getCitizenComplaints(String citizenEmail) {
        User citizen = userRepository.findByEmail(citizenEmail)
                .orElseThrow(() -> new IllegalArgumentException("Citizen not found"));
        return complaintRepository.findByCitizenIdOrderByDateDesc(citizen.getId());
    }

    @Override
    public List<Complaint> getWorkerComplaints(String workerEmail) {
        User worker = userRepository.findByEmail(workerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));
        return complaintRepository.findByWorkerIdOrderByDateDesc(worker.getId());
    }

    @Override
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByDateDesc();
    }

    @Override
    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with ID: " + id));
    }

    @Override
    public Complaint assignWorker(Long complaintId, Long workerId, String priority) {
        Complaint complaint = getComplaintById(complaintId);
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found with ID: " + workerId));

        if (!"ROLE_WORKER".equals(worker.getRole())) {
            throw new IllegalArgumentException("Assigned user is not a worker");
        }

        complaint.setWorker(worker);
        complaint.setPriority(priority);
        complaint.setStatus("ASSIGNED");
        return complaintRepository.save(complaint);
    }

    @Override
    public Complaint updateStatus(Long complaintId, String status, String remarks) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setStatus(status);
        if (remarks != null && !remarks.trim().isEmpty()) {
            complaint.setRemarks(remarks);
        }
        return complaintRepository.save(complaint);
    }

    @Override
    public Complaint completeComplaint(Long complaintId, MultipartFile afterImage, String remarks) {
        Complaint complaint = getComplaintById(complaintId);

        String savedFilename = null;
        if (afterImage != null && !afterImage.isEmpty()) {
            try {
                savedFilename = cloudinaryService.uploadFile(afterImage);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload after-cleaning image to Cloudinary", e);
            }
        }

        complaint.setAfterCleaningImage(savedFilename);
        complaint.setStatus("COMPLETED");
        if (remarks != null && !remarks.trim().isEmpty()) {
            complaint.setRemarks(remarks);
        }
        return complaintRepository.save(complaint);
    }

    @Override
    public long getCountByStatus(String status) {
        return complaintRepository.countByStatus(status);
    }

    @Override
    public long getCountByCitizen(String email) {
        User citizen = userRepository.findByEmail(email).orElse(null);
        if (citizen == null) return 0;
        return complaintRepository.countByCitizenId(citizen.getId());
    }

    @Override
    public org.springframework.data.domain.Page<Complaint> searchAndFilter(String query, String status, org.springframework.data.domain.Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            if (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status)) {
                return complaintRepository.findAll(pageable);
            } else {
                return complaintRepository.findByStatus(status.toUpperCase(), pageable);
            }
        } else {
            String trimmedQuery = query.trim();
            if (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status)) {
                return complaintRepository.searchComplaints(trimmedQuery, pageable);
            } else {
                return complaintRepository.searchComplaintsWithStatus(trimmedQuery, status.toUpperCase(), pageable);
            }
        }
    }

    @Override
    public void deleteComplaint(Long id) {
        if (complaintRepository.existsById(id)) {
            complaintRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Complaint not found with ID: " + id);
        }
    }
}
