package com.monolitomicroservice.teste.performance.common;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public UserVO() {
        this(String.valueOf(System.nanoTime()));
    }

    public UserVO(String userCode) {
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

    public UserVO(Long id, Long tenantId, String userCode, String login, String password, String email,
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

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
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

    @Override
    public String toString() {
        return "UserVO{" +
                "id=" + id +
                ", tenantId=" + tenantId +
                ", userCode='" + userCode + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}
