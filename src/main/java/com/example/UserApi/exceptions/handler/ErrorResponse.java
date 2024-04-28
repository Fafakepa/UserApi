package com.example.UserApi.exceptions.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String timestamp;
    private String statusCode;
    private String title;
    private String detail;
}
