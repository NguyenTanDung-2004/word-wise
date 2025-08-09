package com.example.WordWise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum KeyConfigEnum {
    SETUP_DATA(1, "SETUP_DATA");
    private int id;
    private String key;

    public static KeyConfigEnum fromId(String key) {
        for (KeyConfigEnum keyConfig : KeyConfigEnum.values()) {
            if (keyConfig.getKey().equals(key)) {
                return keyConfig;
            }
        }
        throw new IllegalArgumentException("No KeyConfigEnum found for key: " + key);
    }
}
