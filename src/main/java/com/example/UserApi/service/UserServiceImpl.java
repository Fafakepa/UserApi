package com.example.UserApi.service;

import com.example.UserApi.config.MyConfig;
import com.example.UserApi.dto.UserRequestDto;
import com.example.UserApi.exceptions.InvalidAgeException;
import com.example.UserApi.exceptions.InvalidInputException;
import com.example.UserApi.model.User;
import com.example.UserApi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyConfig ageConfig;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id: " + id + " is not found"));
    }

    @Override
    public User saveUser(User user) {
        if (!isValidAge(user.getBirthDate())) {
            throw new InvalidAgeException(String.format("Minimum age should be at least %d years",
                    ageConfig.getMinimumAge()));
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, UserRequestDto updatedUser) {
        User userFromDb = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id: " + id + " is not found"));
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(updatedUser, userFromDb);
        return userRepository.save(userFromDb);
    }

    @Override
    public User patchUser(Long id, UserRequestDto dto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id: " + id + " is not found"));
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(dto, user);
        isValidAge(user.getBirthDate());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> findUsersByBirthDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new InvalidInputException("[From] is after [to]");
        }
        return userRepository.findByBirthDateBetween(from, to);
    }

    private boolean isValidAge(LocalDate birthDate) {
        LocalDate eighteenYearsAgo = LocalDate.now().minusYears(ageConfig.getMinimumAge());
        return birthDate.isBefore(eighteenYearsAgo);
    }
}
