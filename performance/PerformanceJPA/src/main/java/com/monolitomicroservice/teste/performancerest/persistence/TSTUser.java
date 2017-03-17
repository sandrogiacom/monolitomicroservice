package com.monolitomicroservice.teste.performancerest.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "TST_USER")
@NamedQueries({
        @NamedQuery(name = "TSTUser.findByCode", query = "SELECT b FROM TSTUser b WHERE b.userCode = :userCode"),
        @NamedQuery(name = "TSTUser.findByLogin", query = "SELECT b FROM TSTUser b WHERE b.login = :login"),
        @NamedQuery(name = "TSTUser.findByRange", query = "SELECT b FROM TSTUser b ORDER BY b.fullName")
})
@XmlRootElement(name = "user")
public class TSTUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "TENANT_ID", nullable = false, insertable = true, updatable = false)
    private Long tenantId;

    @Basic(optional = true)
    @Size(max = 150)
    @Column(name = "USER_CODE", length = 150, unique = true)
    private String userCode;

    @Column(name = "LOGIN", length = 150, nullable = false, unique = true)
    private String login;

    @Size(max = 512)
    @Column(name = "PASSWORD", length = 512)
    private String password;

    @Basic(optional = false)
    @Size(min = 0, max = 120)
    @Column(name = "EMAIL", length = 120, nullable = false)
    private String email;

    @Size(min = 1, max = 255)
    @Column(name = "FIRST_NAME", length = 255, nullable = true)
    private String firstName;

    @Size(min = 1, max = 255)
    @Column(name = "LAST_NAME", length = 255, nullable = true)
    private String lastName;

    @Size(min = 1, max = 255)
    @Column(name = "FULL_NAME", length = 255, nullable = true)
    private String fullName;

    @Basic(optional = true)
    @Column(name = "BIRTH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthDate;

    public TSTUser() {
        this(String.valueOf(System.nanoTime()));
    }

    public TSTUser(String userCode) {
        this.tenantId = 1l;
        this.userCode = userCode;
        this.login = this.userCode;
        this.password = this.userCode;
        this.email = this.userCode + "@teste.com";
        this.firstName = this.userCode;
        this.lastName = String.valueOf(System.currentTimeMillis());
        this.fullName = this.firstName + " " + this.lastName;
        this.birthDate = new Date();
    }

    public TSTUser(Long id, Long tenantId, String userCode, String login, String password, String email,
            String firstName, String lastName, String fullName, Date birthDate) {
        this.id = id;
        this.tenantId = tenantId;
        this.userCode = userCode;
        this.login = login;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.birthDate = birthDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TSTUser tstUser = (TSTUser) o;
        return Objects.equals(login, tstUser.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    @Override
    public String toString() {
        return "TSTUser {" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", birthDate=" + birthDate +
                ", tenantId=" + tenantId +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", userCode='" + userCode + '\'' +
                '}';
    }
}
