package com.mariuszilinskas.vsp.userservice.dto;

import java.util.List;
import java.util.UUID;

public record AuthDetailsResponse(
        UUID userId,
        List<String> roles,
        List<String> authorities
) {}
