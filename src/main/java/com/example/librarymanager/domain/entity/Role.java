package com.example.librarymanager.domain.entity;

import com.example.librarymanager.constant.RoleConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles",
        uniqueConstraints = @UniqueConstraint(name = "UN_ROLE_CODE", columnNames = "code"))
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Byte id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false)
    private RoleConstant code;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserGroupRole> userGroupRoles = new ArrayList<>();

    public Role(RoleConstant roleConstant) {
        this.name = roleConstant.getRoleName();
        this.code = roleConstant;
    }
}