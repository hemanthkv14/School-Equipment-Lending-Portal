package com.school.validator;
import com.school.dto.EquipmentDto;

import java.util.function.BiFunction;

public class EquipmentValidator {

    private static final BiFunction<EquipmentDto, Boolean, Boolean> validateQuantity = (equipmentDto, isUpdate) -> {
        if (equipmentDto == null) return false;
        Integer total = equipmentDto.getTotalQuantity();
        Integer available = equipmentDto.getQuantityAvailable();

        if (isUpdate) {
            return total != null && total > 0;
        } else {
            return total != null && total > 0 && available!= null && available > 0 && total.equals(available);
        }
    };

    public static boolean isValidEquipmentAddRequest(EquipmentDto equipmentDto) {
        return checkInputFields(equipmentDto) && validateQuantity.apply(equipmentDto, false);
    }

    public static boolean isValidEquipmentUpdateRequest(EquipmentDto equipmentDTO) {
        return checkInputFields(equipmentDTO)
                && equipmentDTO.getEquipmentId() != null
                && validateQuantity.apply(equipmentDTO, true);
    }

    private static boolean checkInputFields(EquipmentDto equipmentDto) {
        return equipmentDto != null
                && StringUtil.isNotBlank(equipmentDto.getName())
                && equipmentDto.getCategoryId() != null
                && equipmentDto.getTotalQuantity() != null
                && equipmentDto.getTotalQuantity() > 0;
    }
}

