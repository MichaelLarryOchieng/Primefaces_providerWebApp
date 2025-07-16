/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;


/**
 *
 * @author michael
 */
@Named("UserBean")
@ViewScoped
public class UserBean implements Serializable {

    private List<User> userList;
    private List<User> selectedUsers;
    

    @PostConstruct
    public void init() {
        System.out.println("UserBean @PostConstruct: Initializing user list...");
        refreshUserList();

        if (selectedUsers == null) {
            selectedUsers = new ArrayList<>();
        }
    }

    public void refreshUserList() {
        userList = new ArrayList<>();
        Connection conn = null; 
        PreparedStatement pstmt = null; 
        ResultSet rs = null;           

        try {
            conn = mysqlConnector.getConnection(); 

            String sql = "SELECT id, name, username, password, department, birth, gender, city, region_id FROM users";
            pstmt = conn.prepareStatement(sql);
            
            rs = pstmt.executeQuery();

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setName(rs.getString("name"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setDepartment(rs.getString("department"));
                u.setBirth(rs.getDate("birth")); 
                u.setGender(rs.getString("gender"));
                u.setCity(rs.getString("city"));
                u.setRegion_id(rs.getString("region_id"));
                userList.add(u);
                
            }
            System.out.println("UserBean refreshed: " + userList.size() + " users loaded.");
        } catch (SQLException e) {
            System.err.println("SQL Error while refreshing user list: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Database Error", "Could not load user list: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("Unexpected error while refreshing user list: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "An unexpected error occurred: " + e.getMessage()));
        } finally {
            // Manually close resources in the finally block
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error closing ResultSet in UserBean: " + e.getMessage());
                    // Consider logging this or adding to FacesMessage if critical
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    System.err.println("Error closing PreparedStatement in UserBean: " + e.getMessage());
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing Connection in UserBean: " + e.getMessage());
                }
            }
        }
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<User> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }
    
    
    
}
