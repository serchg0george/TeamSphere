package com.teamsphere.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "t_companies")
public class CompanyEntity extends BaseEntity {

    @NotBlank
    @Column(name = "company_name", nullable = false, length = 50)
    private String name;

    @NotBlank
    @Column(name = "industry", nullable = false, length = 50)
    private String industry;

    @NotBlank
    @Column(name = "address", length = 50)
    private String address;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "company", orphanRemoval = true)
    private List<ProjectEntity> projects;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyEntity that)) return false;
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
