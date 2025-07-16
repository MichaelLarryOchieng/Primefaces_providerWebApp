/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

import jakarta.inject.Inject; 
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date; 

/**
 *
 * @author michael
 */
@Named("actionBean")
@ViewScoped
public class ActionBean implements Serializable {

    
    @Inject
    private UserBean userBean; 

    private User selectedUser;
    private mysqlConnector mysqlConnector = new mysqlConnector(); 

    
    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    
    public void prepareAddUser() {
        this.selectedUser = new User();
    }

    public void prepareEditUser(User userToEdit) {
        this.selectedUser = userToEdit.clone();
    }

    public void saveUserAction() {
        

        boolean isNewUser = (selectedUser.getId() == null);
        String sql;
        if (isNewUser) {
            sql = "INSERT INTO users (name, username, password, department, birth, gender, city, region_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
           
            sql = "UPDATE users SET name = ?, username = ?, password = ?, department = ?, birth = ?, gender = ?, city = ?, region_id = ? WHERE id = ?";
        }

        try (Connection conn = mysqlConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, selectedUser.getName());
            pstmt.setString(2, selectedUser.getUsername());
            pstmt.setString(3, selectedUser.getPassword());
            pstmt.setString(4, selectedUser.getDepartment());


            if (selectedUser.getBirth() != null) {
                
                pstmt.setTimestamp(5, new Timestamp(selectedUser.getBirth().getTime())); // Use Timestamp for best compatibility
             
            } else {
                pstmt.setNull(5, Types.TIMESTAMP); 
            }
            

            pstmt.setString(6, selectedUser.getGender());
            pstmt.setString(7, selectedUser.getCity());
            pstmt.setString(8, selectedUser.getRegion_id());

            if (!isNewUser) {
                pstmt.setLong(9, selectedUser.getId());
            }

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                if (isNewUser) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            selectedUser.setId(generatedKeys.getLong(1));
                        }
                    }
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "User added successfully!"));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "User updated successfully!"));
                }
               
                userBean.refreshUserList(); 
                this.selectedUser = new User();
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Failed", "No changes made or user not found."));
            }

        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Database Error", "An error occurred during database operation: " + e.getMessage()));
            System.err.println("SQL Error during user save: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "An unexpected error occurred: " + e.getMessage()));
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteUserAction() {
       
        if (selectedUser != null && selectedUser.getId() != null) {
            String sql = "DELETE FROM users WHERE id = ?";
            try (Connection conn = mysqlConnector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, selectedUser.getId());
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "User deleted successfully!"));
                   
                    userBean.refreshUserList(); 
                    selectedUser = null; 
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Failed", "User not found for deletion."));
                }
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Database Error", "Error deleting user: " + e.getMessage()));
                System.err.println("SQL Error during user deletion: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "No user selected for deletion."));
        }
    }
}
