package ru.practicum.users.dto;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@ToString
public class NewUserDto {

    @NotBlank
    @Email
    @Size(max = 254, min = 6)
    private String email;

    @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
    @Size(max = 250, min = 2)
    private String name;
}