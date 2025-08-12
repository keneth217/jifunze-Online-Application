package com.jifunze.online.servicesImpl;

import com.jifunze.online.dtos.StudentRegistrationRequest;
import com.jifunze.online.dtos.StudentRegistrationResponse;
import com.jifunze.online.entity.Students;
import com.jifunze.online.entity.User;
import com.jifunze.online.repos.StudentsRepository;
import com.jifunze.online.repos.UserRepository;
import com.jifunze.online.services.StudentService;
import com.jifunze.online.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    
    @Autowired
    private StudentsRepository studentsRepository;
    
    @Autowired
    private UserRepository userRepository;

    private  final PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserService userService;

    public StudentServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public StudentRegistrationResponse registerStudent(StudentRegistrationRequest request) {
        try {
            // Check if user already exists by phone number
            if (request.getPhoneNumber() != null && userService.existsByPhoneNumber(request.getPhoneNumber())) {
                return StudentRegistrationResponse.builder()
                    .message("User with this phone number already exists")
                    .status("error")
                    .build();
            }
            
            if (request.getPhoneNumber() != null && studentsRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                return StudentRegistrationResponse.builder()
                    .message("Student with this phone number already exists")
                    .status("error")
                    .build();
            }
            
            // Generate email from phone number
            String email = request.getPhoneNumber() + "@jifunze.online";
            
            // Create User entity
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setRole(User.UserRole.STUDENT);
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setHasActiveSubscription(false);
            user.setCommissionRate(new java.math.BigDecimal("0.00")); // Students have no commission
            
            // Save User
            User savedUser = userRepository.save(user);
            
            // Create Students entity
            Students student = new Students();
            student.setName(request.getFullName());
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
            return StudentRegistrationResponse.builder()
                .message("Student registration successful")
                .status("success")
                .studentId(savedStudent.getId())
                .fullName(savedUser.getFullName())
                .phoneNumber(savedUser.getPhoneNumber())
                .build();
            
        } catch (Exception e) {
            return StudentRegistrationResponse.builder()
                .message("Registration failed: " + e.getMessage())
                .status("error")
                .build();
        }
    }
    
    @Override
    public Students getStudentById(Long id) {
        Optional<Students> student = studentsRepository.findById(id);
        return student.orElse(null);
    }
    
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return studentsRepository.existsByPhoneNumber(phoneNumber);
    }
    
    @Override
    public java.util.List<Students> getAll() {
        return studentsRepository.findAll();
    }
} 