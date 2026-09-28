package com.smartwaste.repository;

import com.smartwaste.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    List<Complaint> findByCitizenIdOrderByDateDesc(Long citizenId);
    
    List<Complaint> findByWorkerIdOrderByDateDesc(Long workerId);
    
    List<Complaint> findAllByOrderByDateDesc();
    
    List<Complaint> findByStatus(String status);
    
    long countByStatus(String status);
    
    long countByCitizenId(Long citizenId);
    
    @Query("SELECT c FROM Complaint c WHERE " +
           "LOWER(c.complaintId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.area) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.category) LIKE LOWER(CONCAT('%', :query, '%'))")
    org.springframework.data.domain.Page<Complaint> searchComplaints(@Param("query") String query, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT c FROM Complaint c WHERE c.status = :status AND (" +
           "LOWER(c.complaintId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.area) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')))")
    org.springframework.data.domain.Page<Complaint> searchComplaintsWithStatus(@Param("query") String query, @Param("status") String status, org.springframework.data.domain.Pageable pageable);
    
    org.springframework.data.domain.Page<Complaint> findByStatus(String status, org.springframework.data.domain.Pageable pageable);
    
    org.springframework.data.domain.Page<Complaint> findAll(org.springframework.data.domain.Pageable pageable);
}
