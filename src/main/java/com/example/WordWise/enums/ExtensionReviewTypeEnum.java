package com.example.WordWise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ExtensionReviewTypeEnum {
    TRANSLATE(1, "TRANSLATE"),
    SELECT(2, "SELECT"),
    COMPLETE(3, "COMPLETE");

    private int id;
    private String name;

    public static ExtensionReviewTypeEnum fromId(int id) {
        for (ExtensionReviewTypeEnum type : ExtensionReviewTypeEnum.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("No ExtensionReviewType found for id: " + id);
    }
}
