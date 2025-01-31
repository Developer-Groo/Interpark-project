package org.example.interpark.domain.user.Dto;

public record UserRequestDto(
        String username,
        String password,
        String email
) {

}
