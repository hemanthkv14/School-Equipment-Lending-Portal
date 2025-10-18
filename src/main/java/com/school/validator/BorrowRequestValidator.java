package com.school.validator;

import com.school.dto.BorrowRequestDTO;

public class BorrowRequestValidator {

    public static boolean isValidBorrowCreateRequest(BorrowRequestDTO borrowRequestDTO) {
        return borrowRequestDTO != null && borrowRequestDTO.getRequestedBy() != null && borrowRequestDTO.getEquipmentId() != null && borrowRequestDTO.getQuantity() > 0;
    }
}
