package com.monolitomicroservice.teste.userjpa.persistence;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "common_user_role", uniqueConstraints = {
        @UniqueConstraint(name = "uk_userrole001", columnNames = {"login", "role_code"})
})
@NamedQueries({
        @NamedQuery(name = "CommonUserRole.findByLogin", query = "SELECT b FROM CommonUserRole b WHERE b.login = :login"),
        @NamedQuery(name = "CommonUserRole.findByRole", query = "SELECT b FROM CommonUserRole b WHERE b.roleCode = :roleCode"),
        @NamedQuery(name = "CommonUserRole.findByUnique", query = "SELECT b FROM CommonUserRole b WHERE b.login = :login AND b.roleCode = :roleCode")
})
@XmlRootElement(name = "userrole")
public class CommonUserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_role_id")
    private Long id;

    @Column(name = "login", length = 150, nullable = false, unique = false)
    @Size(max = 150)
    private String login;

    @Column(name = "role_code", length = 150, nullable = false, unique = false)
    @Size(max = 150)
    private String roleCode;

    public CommonUserRole() {
    }

    public CommonUserRole(Long id) {
        this.id = id;
    }

    public CommonUserRole(String login, String roleCode) {
        this.login = login;
        this.roleCode = roleCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CommonUserRole that = (CommonUserRole) o;
        return Objects.equals(login, that.login) &&
                Objects.equals(roleCode, that.roleCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, roleCode);
    }

    @Override
    public String toString() {
        return "CommonUserRole {" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", roleCode='" + roleCode + '\'' +
                '}';
    }
}
