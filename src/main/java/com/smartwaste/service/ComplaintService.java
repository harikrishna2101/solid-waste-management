package com.smartwaste.service;

import com.smartwaste.dto.ComplaintDto;
import com.smartwaste.entity.Complaint;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ComplaintService {

    Complaint raiseComplaint(ComplaintDto complaintDto, String citizenEmail);

    List<Complaint> getCitizenComplaints(String citizenEmail);

    List<Complaint> getWorkerComplaints(String workerEmail);

    List<Complaint> getAllComplaints();

    Complaint getComplaintById(Long id);

    Complaint assignWorker(Long complaintId, Long workerId, String priority);

    Complaint updateStatus(Long complaintId, String status, String remarks);

    Complaint completeComplaint(Long complaintId, MultipartFile afterImage, String remarks);

    long getCountByStatus(String status);

    long getCountByCitizen(String email);

    org.springframework.data.domain.Page<Complaint> searchAndFilter(String query, String status, org.springframework.data.domain.Pageable pageable);

    void deleteComplaint(Long id);
}
