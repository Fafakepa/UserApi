package com.example.UserApi.service;

import com.example.UserApi.dto.UserRequestDto;
import com.example.UserApi.model.User;

import java.time.LocalDate;
import java.util.List;

public interface UserService {

    User findUserById(Long id);

    User saveUser(User user);

    User updateUser(Long id, UserRequestDto user);

    User patchUser(Long id, UserRequestDto user);

    void deleteUser(Long id);

    List<User> findUsersByBirthDateRange(LocalDate from, LocalDate to);
}
