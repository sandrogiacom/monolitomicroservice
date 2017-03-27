package com.monolitomicroservice.teste.sessionrest.persistence;

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
@Table(name = "SESSION_USER_ROLE", uniqueConstraints = {
        @UniqueConstraint(name = "UK_USERROLE001", columnNames = {"LOGIN", "ROLE_CODE"})
})
@NamedQueries({
        @NamedQuery(name = "SessionUserRole.findByLogin", query = "SELECT b FROM SessionUserRole b WHERE b.login = :login"),
        @NamedQuery(name = "SessionUserRole.findByRole", query = "SELECT b FROM SessionUserRole b WHERE b.roleCode = :roleCode"),
        @NamedQuery(name = "SessionUserRole.findByUnique", query = "SELECT b FROM SessionUserRole b WHERE b.login = :login AND b.roleCode = :roleCode")
})
@XmlRootElement(name = "userrole")
public class SessionUserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ROLE_ID")
    private Long id;

    @Column(name = "LOGIN", length = 150, nullable = false, unique = false)
    @Size(max = 150)
    private String login;

    @Column(name = "ROLE_CODE", length = 150, nullable = false, unique = false)
    @Size(max = 150)
    private String roleCode;

    public SessionUserRole() {
    }

    public SessionUserRole(Long id) {
        this.id = id;
    }

    public SessionUserRole(String login, String roleCode) {
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
        SessionUserRole that = (SessionUserRole) o;
        return Objects.equals(login, that.login) &&
                Objects.equals(roleCode, that.roleCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, roleCode);
    }

    @Override
    public String toString() {
        return "SessionUserRole {" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", roleCode='" + roleCode + '\'' +
                '}';
    }
}
