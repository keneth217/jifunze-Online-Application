package com.jifunze.online.servicesImpl;

import com.jifunze.online.dtos.StudentRegistrationRequest;
import com.jifunze.online.entity.Students;
import com.jifunze.online.entity.User;
import com.jifunze.online.repos.StudentsRepository;
import com.jifunze.online.repos.UserRepository;
import com.jifunze.online.services.StudentService;
import com.jifunze.online.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class StudentServiceImpl implements StudentService {
    
    @Autowired
    private StudentsRepository studentsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @Override
    public Map<String, Object> registerStudent(StudentRegistrationRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user already exists in either table
            if (userService.existsByEmail(request.getEmail())) {
                response.put("message", "User with this email already exists");
                response.put("status", "error");
                return response;
            }
            
            if (request.getPhoneNumber() != null && userService.existsByPhoneNumber(request.getPhoneNumber())) {
                response.put("message", "User with this phone number already exists");
                response.put("status", "error");
                return response;
            }
            
            if (studentsRepository.existsByEmail(request.getEmail())) {
                response.put("message", "Student with this email already exists");
                response.put("status", "error");
                return response;
            }
            
            if (request.getPhoneNumber() != null && studentsRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                response.put("message", "Student with this phone number already exists");
                response.put("status", "error");
                return response;
            }
            
            // Create User entity
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setFullName(request.getFullName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setRole(User.UserRole.STUDENT);
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setHasActiveSubscription(false);
            
            // Save User
            User savedUser = userRepository.save(user);
            
            // Create Students entity
            Students student = new Students();
            student.setName(request.getFullName());
            student.setEmail(request.getEmail());
            student.setPhoneNumber(request.getPhoneNumber());
            student.setPassword(request.getPassword());
            student.setRole(request.getRole());
            student.setStatus(request.getStatus());
            
            // Set timestamps
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            student.setCreatedAt(currentTime);
            student.setUpdatedAt(currentTime);
            
            // Save Student
            Students savedStudent = studentsRepository.save(student);
            
            // Prepare response
            response.put("message", "Student registration successful");
            response.put("status", "success");
            response.put("userId", savedUser.getId());
            response.put("studentId", savedStudent.getId());
            response.put("email", savedUser.getEmail());
            response.put("fullName", savedUser.getFullName());
            
        } catch (Exception e) {
            response.put("message", "Registration failed: " + e.getMessage());
            response.put("status", "error");
        }
        
        return response;
    }
    
    @Override
    public Students getStudentById(Long id) {
        return studentsRepository.findById(id).orElse(null);
    }
    
    @Override
    public Students getStudentByEmail(String email) {
        return studentsRepository.findByEmail(email).orElse(null);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return studentsRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return studentsRepository.existsByPhoneNumber(phoneNumber);
    }
} 