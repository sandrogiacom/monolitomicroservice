package com.monolitomicroservice.teste.userjpa.persistence;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "common_user")
@NamedQueries({
        @NamedQuery(name = "CommonUser.findByLogin", query = "SELECT b FROM CommonUser b WHERE b.login = :login")
})
@XmlRootElement(name = "user")
public class CommonUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "login", length = 150, nullable = false, unique = true)
    @Size(max = 150)
    private String login;

    @Size(max = 512)
    @Column(name = "password", length = 512)
    private String password;

    @Size(min = 1, max = 255)
    @Column(name = "full_name", length = 255, nullable = true)
    private String fullName;

    public CommonUser() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CommonUser that = (CommonUser) o;
        return Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    @Override
    public String toString() {
        return "CommonUser {" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
