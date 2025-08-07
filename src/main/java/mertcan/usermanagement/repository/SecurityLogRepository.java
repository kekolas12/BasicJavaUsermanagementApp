package mertcan.usermanagement.repository;

import mertcan.usermanagement.entity.SecurityLog;
import mertcan.usermanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SecurityLogRepository extends JpaRepository<SecurityLog, Long> {
    
    Page<SecurityLog> findByUser(User user, Pageable pageable);
    
    Page<SecurityLog> findByAction(String action, Pageable pageable);
    
    @Query("SELECT sl FROM SecurityLog sl WHERE sl.createdAt BETWEEN :startDate AND :endDate")
    Page<SecurityLog> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate, 
                                           Pageable pageable);
    
    @Query("SELECT sl FROM SecurityLog sl WHERE sl.user = :user AND sl.action = :action")
    Page<SecurityLog> findByUserAndAction(@Param("user") User user, 
                                        @Param("action") String action, 
                                        Pageable pageable);
}
