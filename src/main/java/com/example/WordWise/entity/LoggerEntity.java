package com.example.WordWise.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "logger_entity")
public class LoggerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    private String level;

    private String className;
    private String methodName;

    private String exceptionType;

    @Column(columnDefinition = "text")
    private String exceptionMessage;

    @Column(columnDefinition = "text")
    private String stackTrace;

    @Column(columnDefinition = "text")
    private String threadName;

    // Optional fields
    private String userId;

    @Column(columnDefinition = "text")
    private String requestUrl;

    @Column(columnDefinition = "text")
    private String requestId;

    @Column(columnDefinition = "text")
    private String params;
}
