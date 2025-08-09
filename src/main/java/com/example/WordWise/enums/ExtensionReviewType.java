package com.example.WordWise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ExtensionReviewType {
    TRANSLATE(1, "TRANSLATE"),
    SELECT(2, "SELECT"),
    COMPLETE(3, "COMPLETE");

    private int id;
    private String name;

    public static ExtensionReviewType fromId(int id) {
        for (ExtensionReviewType type : ExtensionReviewType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("No ExtensionReviewType found for id: " + id);
    }
}
