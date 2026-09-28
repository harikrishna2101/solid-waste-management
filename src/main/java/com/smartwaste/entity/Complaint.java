package com.smartwaste.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complaint_id", nullable = false, unique = true)
    private String complaintId; // Unique formatted ID: e.g. COMP-102432

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category; // Organic, Plastic, E-waste, Hazardous, Mixed

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String area;

    private String landmark;

    @Column(nullable = false)
    private String phone;

    private Double latitude;

    private Double longitude;

    @Column(name = "image_path")
    private String imagePath; // Before cleaning image URL/Path

    @Column(name = "after_cleaning_image")
    private String afterCleaningImage; // After cleaning image URL/Path

    private String priority; // Low, Medium, High (set by Admin)

    @Column(nullable = false)
    private String status; // PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, REJECTED

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User citizen; // Reporter

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_worker_id")
    private User worker; // Assigned worker

    @Column(length = 500)
    private String remarks; // Feedback or rejection reasons

    @PrePersist
    protected void onCreate() {
        this.date = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
    }

    // Default Constructor
    public Complaint() {
    }

    // All-args Constructor
    public Complaint(Long id, String complaintId, String title, String category, String description, String area, 
                     String landmark, String phone, Double latitude, Double longitude, String imagePath, 
                     String afterCleaningImage, String priority, String status, LocalDateTime date, 
                     User citizen, User worker, String remarks) {
        this.id = id;
        this.complaintId = complaintId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.area = area;
        this.landmark = landmark;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imagePath = imagePath;
        this.afterCleaningImage = afterCleaningImage;
        this.priority = priority;
        this.status = status;
        this.date = date;
        this.citizen = citizen;
        this.worker = worker;
        this.remarks = remarks;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getAfterCleaningImage() { return afterCleaningImage; }
    public void setAfterCleaningImage(String afterCleaningImage) { this.afterCleaningImage = afterCleaningImage; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public User getCitizen() { return citizen; }
    public void setCitizen(User citizen) { this.citizen = citizen; }

    public User getWorker() { return worker; }
    public void setWorker(User worker) { this.worker = worker; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    // Static Builder
    public static ComplaintBuilder builder() {
        return new ComplaintBuilder();
    }

    public static class ComplaintBuilder {
        private Long id;
        private String complaintId;
        private String title;
        private String category;
        private String description;
        private String area;
        private String landmark;
        private String phone;
        private Double latitude;
        private Double longitude;
        private String imagePath;
        private String afterCleaningImage;
        private String priority;
        private String status;
        private LocalDateTime date;
        private User citizen;
        private User worker;
        private String remarks;

        ComplaintBuilder() {}

        public ComplaintBuilder id(Long id) { this.id = id; return this; }
        public ComplaintBuilder complaintId(String complaintId) { this.complaintId = complaintId; return this; }
        public ComplaintBuilder title(String title) { this.title = title; return this; }
        public ComplaintBuilder category(String category) { this.category = category; return this; }
        public ComplaintBuilder description(String description) { this.description = description; return this; }
        public ComplaintBuilder area(String area) { this.area = area; return this; }
        public ComplaintBuilder landmark(String landmark) { this.landmark = landmark; return this; }
        public ComplaintBuilder phone(String phone) { this.phone = phone; return this; }
        public ComplaintBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public ComplaintBuilder longitude(Double longitude) { this.longitude = longitude; return this; }
        public ComplaintBuilder imagePath(String imagePath) { this.imagePath = imagePath; return this; }
        public ComplaintBuilder afterCleaningImage(String afterCleaningImage) { this.afterCleaningImage = afterCleaningImage; return this; }
        public ComplaintBuilder priority(String priority) { this.priority = priority; return this; }
        public ComplaintBuilder status(String status) { this.status = status; return this; }
        public ComplaintBuilder date(LocalDateTime date) { this.date = date; return this; }
        public ComplaintBuilder citizen(User citizen) { this.citizen = citizen; return this; }
        public ComplaintBuilder worker(User worker) { this.worker = worker; return this; }
        public ComplaintBuilder remarks(String remarks) { this.remarks = remarks; return this; }

        public Complaint build() {
            return new Complaint(id, complaintId, title, category, description, area, landmark, phone, 
                                 latitude, longitude, imagePath, afterCleaningImage, priority, status, 
                                 date, citizen, worker, remarks);
        }
    }
}
