package com.jifunze.online.controllers;

import com.jifunze.online.dtos.StudentRegistrationRequest;
import com.jifunze.online.dtos.StudentRegistrationResponse;
import com.jifunze.online.dtos.StudentResponse;
import com.jifunze.online.entity.Students;
import com.jifunze.online.services.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/register")
    public ResponseEntity<StudentRegistrationResponse> registerStudent(@RequestBody StudentRegistrationRequest studentRegistrationRequest) {
        try {
            StudentRegistrationResponse response = studentService.registerStudent(studentRegistrationRequest);
            if ("success".equals(response.getStatus())) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            StudentRegistrationResponse errorResponse = StudentRegistrationResponse.builder()
                .message("Registration failed: " + e.getMessage())
                .status("error")
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<java.util.List<Students>> getAllStudent(){
        java.util.List<Students> students = studentService.getAll();
        return ResponseEntity.ok(students);
    }


    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        try {
            Students student = studentService.getStudentById(id);
            if (student != null) {
                StudentResponse studentResponse = StudentResponse.builder()
                    .fullName(student.getName())
                    .phoneNumber(student.getPhoneNumber())
                    .role(student.getRole())
                    .status(student.getStatus())
                    .build();
                return ResponseEntity.ok(studentResponse);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check-phone/{phoneNumber}")
    public ResponseEntity<Map<String, Boolean>> checkPhoneExists(@PathVariable String phoneNumber) {
        try {
            boolean exists = studentService.existsByPhoneNumber(phoneNumber);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
