package com.example.event.authentication;

import java.util.List;

import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUserDTO {
    @NonNull
    private String token;
    private List<String> roles;
}
