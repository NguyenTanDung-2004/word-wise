package com.example.WordWise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum CrawlWebTypeEnum {
    VTV("VTV");
    private String id;

    public static EmailTypeEnum fromId(int id) {
        for (EmailTypeEnum type : EmailTypeEnum.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("No EmailTypeEnum found for id: " + id);
    }
}
