package ru.practicum.users.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserDto {

    @Email
    @NotBlank
    @Size(max = 254, min = 6)
    String email;

    @NotBlank
    @Size(max = 250, min = 2)
    String name;
}