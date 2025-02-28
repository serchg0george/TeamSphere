package com.api.management.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_departments")
public class DepartmentEntity extends BaseEntity {

    @Column(name = "group_name", nullable = false, length = 40)
    private String groupName;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE}, mappedBy = "department")
    private EmployeeEntity memberEntity;

}
