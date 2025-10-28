package com.school.dto;

import com.school.enums.LendingStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LendingDto {

    private Long lendingId;
    private Long itemId;
    private String equipmentName;
    private Long borrowerId;
    private String borrowerUsername;
    private LocalDateTime issueDate;
    private LendingStatus approvalStatus;
}