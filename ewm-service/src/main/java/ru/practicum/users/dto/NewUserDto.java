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

    @NotBlank
    @Email
    @Size(max = 254, min = 6)
    String email;

    @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
    @Size(max = 250, min = 2)
    String name;
}
