package com.example.librarymanager.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_group_roles")
public class UserGroupRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_group_role_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id",
            foreignKey = @ForeignKey(name = "USER_GROUP_ROLES_ROLE_ID"),
            referencedColumnName = "role_id",
            nullable = false)
    @JsonIgnore
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_group_id",
            foreignKey = @ForeignKey(name = "USER_GROUP_ROLES_USER_GROUP_ID"),
            referencedColumnName = "user_group_id",
            nullable = false)
    @JsonIgnore
    private UserGroup userGroup;

    public UserGroupRole(Role role, UserGroup userGroup) {
        this.role = role;
        this.userGroup = userGroup;
    }
}
