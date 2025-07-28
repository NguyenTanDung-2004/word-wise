package com.example.WordWise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Array;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum RoleEnum {
    ADMIN("ADMIN", new PermissionEnum[]{PermissionEnum.READ, PermissionEnum.WRITE}),
    USER("USER", new PermissionEnum[]{PermissionEnum.READ});

    private String id;
    private PermissionEnum[] permission;

    public static RoleEnum fromId(String id) {
        for (RoleEnum type : RoleEnum.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No EmailTypeEnum found for id: " + id);
    }

}
