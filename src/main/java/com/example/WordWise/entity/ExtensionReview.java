package com.example.WordWise.entity;

import com.example.WordWise.enums.ExtensionReviewTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "extension_review")
public class ExtensionReview {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private LocalDateTime testDate;
    private Boolean isTrue;
    private String eng;
    private String vn;
    private int type;
    private String userId;
    private String wordId; // this field is used to prevent review many times on 1 word.
    private String description;
    private List<String> options;

    public ExtensionReviewTypeEnum getType() {
        return ExtensionReviewTypeEnum.fromId(type);
    }

    public void setExtensionReviewType(ExtensionReviewTypeEnum type) {
        this.type = type.getId();
    }
}
/*
TRANSLATE: vn, eng
SELECTE: options, eng, description

 */
