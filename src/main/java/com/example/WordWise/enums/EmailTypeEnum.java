package com.example.WordWise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum EmailTypeEnum {
    RESET_PASSWORD(1, "Reset Password");
    private int id;
    private String description;

    public static EmailTypeEnum fromId(int id) {
        for (EmailTypeEnum type : EmailTypeEnum.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("No EmailTypeEnum found for id: " + id);
    }
}
