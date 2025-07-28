package com.example.WordWise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PermissionEnum {
    READ("READ"),
    WRITE("WRITE");

    private String id;
    public static PermissionEnum fromId(String id) {
        for (PermissionEnum type : PermissionEnum.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No EmailTypeEnum found for id: " + id);
    }
}
