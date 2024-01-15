package ru.practicum.users.dto;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String email;
    private Long id;
    private String name;
}