package com.example.WordWise.entity;

import com.example.WordWise.enums.KeyConfigEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "config")
public class Config {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String value;
    private String type;

    public KeyConfigEnum getType() {
        return KeyConfigEnum.fromId(type);
    }
}
