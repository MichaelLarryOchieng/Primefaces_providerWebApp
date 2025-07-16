/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2; 

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("ThemeBean")
@SessionScoped
public class ThemeBean implements Serializable {
    
    private String currentTheme;
    
    @PostConstruct
    public void init() {
        
        String contextTheme = FacesContext.getCurrentInstance()
            .getExternalContext()
            .getInitParameter("primefaces.THEME");
        this.currentTheme = (contextTheme != null) ? contextTheme : "saga";
    }
    
    public void changeTheme(String newTheme) {
        this.currentTheme = newTheme;
        
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Theme Changed", "Theme set to: " + newTheme));
    }
    
    public String getTheme() {
        return currentTheme;
    }
    
    public void setTheme(String theme) {
        this.currentTheme = theme;
    }
    
    public String getThemeCSS() {
        return "primefaces-" + currentTheme;
    }
}
