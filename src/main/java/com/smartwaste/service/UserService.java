package com.smartwaste.service;

import com.smartwaste.dto.ProfileDto;
import com.smartwaste.dto.RegisterDto;
import com.smartwaste.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UserService extends UserDetailsService {
    
    User registerCitizen(RegisterDto registerDto);
    
    User registerWorker(RegisterDto registerDto);

    void toggleBlockStatus(Long userId);
    
    void deleteUser(Long userId);

    User findByEmail(String email);
    
    User updateProfile(String email, ProfileDto profileDto);
    
    List<User> getAllWorkers();
    
    List<User> getAllUsers();
    
    User saveUser(User user);
    
    User findById(Long id);
}
