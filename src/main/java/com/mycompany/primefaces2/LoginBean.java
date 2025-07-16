/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.mycompany.primefaces2;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.*;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {
    private String username;
    private String password;
    private User newUser;
    private boolean signupDialogVisible;
     private User currentUser;
   

    public LoginBean(){
        username = null;
        password = null;
        newUser = new User();
        signupDialogVisible = false;
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

    public User getNewUser() {
        return newUser;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }

    public boolean isSignupDialogVisible() {
        return signupDialogVisible;
    }

    public void setSignupDialogVisible(boolean signupDialogVisible) {
        this.signupDialogVisible = signupDialogVisible;
    }
    
    public User getCurrentUser(){
        return currentUser;
    }
    
    public void setCurrentUser(User currentUser){
        this.currentUser = currentUser;
    }
    
    

    public String doLogin() {
        try(Connection conn = mysqlConnector.getConnection()) {
            String sql = "Select * from users where username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                currentUser = new User();
                currentUser.setId(rs.getLong("id")); 
                currentUser.setName(rs.getString("name"));
                currentUser.setUsername(rs.getString("username"));
                currentUser.setPassword(rs.getString("password")); 
                currentUser.setDepartment(rs.getString("department"));
                currentUser.setBirth(rs.getDate("birth")); 
                currentUser.setGender(rs.getString("gender"));
                currentUser.setCity(rs.getString("city"));            
                currentUser.setRegion_id(rs.getString("region_id"));
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Login Successful!", "Welcome " + username));
                return "Home.xhtml?faces-redirect=true";
            }else{
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid username or password"));
                return null;
            }
        }catch(SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Database Error", "Login database error: " + e.getMessage()));
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "System Error", "An unexpected error occurred during login: " + e.getMessage()));
            e.printStackTrace();
            return null;
        }
    }

    public void openSignupDialog() {
        this.newUser = new User();
        this.signupDialogVisible = true;
    }

    public void doSignup() {
        FacesContext context = FacesContext.getCurrentInstance();
        try (Connection conn = mysqlConnector.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, newUser.getUsername());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                context.addMessage("signupForm:signupUsername", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Signup Failed", "Username already exists. Please choose a different one."));
                context.getPartialViewContext().getRenderIds().add("signupForm:signupMessages");
                context.getPartialViewContext().getRenderIds().add("signupForm:signupUsername");
                return;
            }

            String sql = "INSERT INTO users (name, username, password, department, birth, gender, city, region_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, newUser.getName());
            stmt.setString(2, newUser.getUsername());
            stmt.setString(3, newUser.getPassword());
            stmt.setString(4, newUser.getDepartment());

            if (newUser.getBirth() != null) {
                stmt.setDate(5, new java.sql.Date(newUser.getBirth().getTime()));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }

            stmt.setString(6, newUser.getGender());
            stmt.setString(7, newUser.getCity());
            stmt.setString(8, newUser.getRegion_id());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Signup Successful!", "You can now log in with your new account."));
                this.signupDialogVisible = false;
                context.getPartialViewContext().getRenderIds().add("loginForm:loginMessages");
                context.getPartialViewContext().getRenderIds().add("signupDialog");
                context.getPartialViewContext().getRenderIds().add("signupForm:signupMessages");
                context.getPartialViewContext().getRenderIds().add("signupForm");
                context.getPartialViewContext().getEvalScripts().add("PF('signupDialogWidget').hide()");
                context.getAttributes().put("signupSuccess", true);
                this.username = null;
                this.password = null;
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Signup Failed", "Could not register user. Please try again."));
                context.getPartialViewContext().getRenderIds().add("signupForm:signupMessages");
            }
        } catch (SQLException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Database Error", "Signup database error: " + e.getMessage()));
            e.printStackTrace();
            context.getPartialViewContext().getRenderIds().add("signupForm:signupMessages");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "System Error", "An unexpected error occurred during signup: " + e.getMessage()));
            e.printStackTrace();
            context.getPartialViewContext().getRenderIds().add("signupForm:signupMessages");
        }
    }

    public String logout() { 
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "login?faces-redirect=true";
    }
}

