package com.teamsphere.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "t_employees")
public class EmployeeEntity extends BaseEntity {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z\\p{IsCyrillic} -]+$")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z\\p{IsCyrillic} -]+$")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Pattern(regexp = "^(?!\\s*$)[-0-9\\s]{10}$")
    @Column(name = "pin", length = 10)
    private String pin;

    @NotBlank
    @Column(name = "address", nullable = false, length = 50)
    private String address;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @ManyToOne(optional = false)
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    @ManyToOne(optional = false)
    @JoinColumn(name = "position_id")
    private PositionEntity position;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<TaskEntity> tasks;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "t_projects_employees",
            joinColumns = {@JoinColumn(name = "employee_id")},
            inverseJoinColumns = {@JoinColumn(name = "project_id")}
    )
    private Set<ProjectEntity> projects;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeEntity that)) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy hProxy ? hProxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hProxy ? hProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hProxy ? hProxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
