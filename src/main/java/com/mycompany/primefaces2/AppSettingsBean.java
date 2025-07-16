/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

/**
 *
 * @author michael
 */
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped; // Or ApplicationScoped if settings are global for all users
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.Part; // For file upload

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.TimeZone;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

@Named("appSettingsBean")
@SessionScoped // Use SessionScoped if settings are per-user, ApplicationScoped if global
public class AppSettingsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String brandingName;
    private String timezone;
    private String language;
    private String defaultView;
    private String currentLogoPath; // Path to the current logo
    private Part logoFile; // For new logo upload

    // Pre-populate with options for dropdowns
    private List<String> availableTimezones;
    private List<String> availableLanguages;
    private List<String> availableViews;

    @PostConstruct
    public void init() {
        // Initialize available options
        availableTimezones = new ArrayList<>();
        // Add common timezones or dynamically fetch them
        availableTimezones.add("Europe/Berlin");
        availableTimezones.add("America/New_York");
        availableTimezones.add("Asia/Tokyo");
        // You can get all available timezones like this:
        // for (String id : TimeZone.getAvailableIDs()) {
        //     availableTimezones.add(id);
        // }


        availableLanguages = new ArrayList<>();
        availableLanguages.add("en"); // English
        availableLanguages.add("de"); // German
        availableLanguages.add("es"); // Spanish
        // You could also use Locale.getAvailableLocales() and format them

        availableViews = new ArrayList<>();
        availableViews.add("overview");
        availableViews.add("userList");
        availableViews.add("myProfile"); // Assuming you might add this page later

        // Load existing settings (for now, default values; in real app, from DB/config file)
        loadSettings();
    }

    public void loadSettings() {
        // In a real application, load these from a database, properties file, or configuration service.
        // For this example, we'll just set some defaults.
        this.brandingName = "Dashboard App";
        this.timezone = "Europe/Berlin"; // Default timezone
        this.language = "en"; // Default language
        this.defaultView = "overview"; // Default dashboard view
        this.currentLogoPath = "/images/Logo.png"; // Default logo path
        addInfoMessage("Settings loaded successfully.");
    }

    public void saveSettings() {
        try {
            // Save branding name, timezone, language, default view
            // In a real app, save to DB or config file
            System.out.println("Saving settings:");
            System.out.println("Branding Name: " + brandingName);
            System.out.println("Timezone: " + timezone);
            System.out.println("Language: " + language);
            System.out.println("Default View: " + defaultView);

            // Handle logo upload if a new file is provided
            if (logoFile != null && logoFile.getSize() > 0) {
                String uploadsDir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("") + File.separator + "images";
                File uploadsFolder = new File(uploadsDir);
                if (!uploadsFolder.exists()) {
                    uploadsFolder.mkdirs(); // Create the directory if it doesn't exist
                }

                String filename = "Logo.png"; // Or generate a unique name
                File newLogoFile = new File(uploadsFolder, filename);

                // Copy the uploaded file to the images directory
                Files.copy(logoFile.getInputStream(), newLogoFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                this.currentLogoPath = "/images/" + filename; // Update path
                addInfoMessage("New logo uploaded successfully.");
            }

            addInfoMessage("Settings saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            addErrorMessage("Error saving settings: " + e.getMessage());
        }
    }

    // Helper methods for FacesMessages
    private void addInfoMessage(String summary) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null));
    }

    private void addErrorMessage(String summary) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
    }


    // --- Getters and Setters ---
    public String getBrandingName() {
        return brandingName;
    }

    public void setBrandingName(String brandingName) {
        this.brandingName = brandingName;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDefaultView() {
        return defaultView;
    }

    public void setDefaultView(String defaultView) {
        this.defaultView = defaultView;
    }

    public String getCurrentLogoPath() {
        return currentLogoPath;
    }

    public void setCurrentLogoPath(String currentLogoPath) {
        this.currentLogoPath = currentLogoPath;
    }

    public Part getLogoFile() {
        return logoFile;
    }

    public void setLogoFile(Part logoFile) {
        this.logoFile = logoFile;
    }

    public List<String> getAvailableTimezones() {
        return availableTimezones;
    }

    public List<String> getAvailableLanguages() {
        return availableLanguages;
    }

    public List<String> getAvailableViews() {
        return availableViews;
    }
}
