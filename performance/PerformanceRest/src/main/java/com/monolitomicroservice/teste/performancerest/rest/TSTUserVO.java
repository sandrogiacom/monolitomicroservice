package com.monolitomicroservice.teste.performancerest.rest;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
public class TSTUserVO implements Serializable {
    private Long id;
    private Long tenantId;
    private String userCode;
    private String login;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private Date birthDate;

    public TSTUserVO() {
        this(String.valueOf(System.nanoTime()));
    }

    public TSTUserVO(String userCode) {
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
        TSTUserVO tstUser = (TSTUserVO) o;
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
