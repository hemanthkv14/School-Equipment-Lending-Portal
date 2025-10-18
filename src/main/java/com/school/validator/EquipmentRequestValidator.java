package com.school.validator;

import com.school.dto.EquipmentDTO;
import com.school.enums.EquipmentCondition;

public class EquipmentRequestValidator {

    public static boolean isValidEquipmentAddRequest(EquipmentDTO equipmentDTO) {
        return equipmentDTO != null && StringUtil.isNotBlank(equipmentDTO.getEquipmentName()) &&
                StringUtil.isNotBlank(equipmentDTO.getCategory()) && equipmentDTO.getQuantityTotal() > 0 &&
                EquipmentCondition.isEquipmentConditionValid(equipmentDTO.getCondition());
    }

    public static boolean isValidEquipmentUpdateRequest(EquipmentDTO equipmentDTO) {
        return isValidEquipmentAddRequest(equipmentDTO) && equipmentDTO.getEquipmentId() != null;
    }
}
