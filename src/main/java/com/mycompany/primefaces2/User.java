/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;
import java.util.Objects;
import java.io.Serializable;
import java.util.Date;


/**
 *
 * @author michael
 */
// User.java
public class User implements Serializable, Cloneable{
   
    private Long id;
    private String name;
    private String username;
    private String password;
    private String department;
    private Date birth;
    private String gender;
    private String city;
    private String region_id;
    
    public User() {
    }

    public User( Long id, String name, String username, String password, String department,Date birth, String gender, String city,
          String region_id) {
        
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.department = department;
        this.birth = birth;
        this.gender = gender;
        this.city = city;
        this.region_id = region_id;
    }

    // Getters and Setters
    
    @Override
    public User clone(){
        return new User( getId(), getName(), getUsername(), getPassword(), getDepartment(), getBirth(), getGender(), getCity(), getRegion_id());
    }
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
    
    public java.util.Date getBirth() {
        return birth;
    }

    public void setBirth(java.util.Date birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }
    
     

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        return Objects.equals(this.id, other.id); // Objects.equals handles nulls safely
    }
}


