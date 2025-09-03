package com.example.WordWise.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.annotation.Generated;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    @Column(name = "user_id")
    private String userId;
    private String userName; 
    private String email;
    private String password;
    private String url;
    private String codeResetPassword;
    private String roleId;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<PracticeTogetherRoom> practiceTogetherRooms = new HashSet<>();
}
