package com.school.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class DueTrackingDto {
    private Long dueId;
    private Long lendingId;
    private LocalDateTime dueDate;
    private Boolean overdue;
    private LocalDateTime returnDate;
}
