package com.school.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class LendingRequestDto {
    private Long itemId;
    private Long borrowerId;
    private Long lendingId;
    private Long issuedById;
    private String returnedCondition;
}