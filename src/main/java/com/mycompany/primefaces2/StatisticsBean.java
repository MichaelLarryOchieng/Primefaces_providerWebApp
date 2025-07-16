/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;


             

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import software.xdev.chartjs.model.charts.PieChart;
import software.xdev.chartjs.model.color.RGBAColor;
import software.xdev.chartjs.model.data.PieData;
import software.xdev.chartjs.model.dataset.PieDataset;
import software.xdev.chartjs.model.options.PieOptions;
import software.xdev.chartjs.model.options.Plugins;
import software.xdev.chartjs.model.options.Title;
import org.primefaces.event.ItemSelectEvent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("statisticsBean")
@RequestScoped
public class StatisticsBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(StatisticsBean.class.getName());
    
    private String departmentPieModel;
    
    
    private static final List<RGBAColor> CHART_COLORS = Arrays.asList(
        new RGBAColor(255, 99, 132),   
        new RGBAColor(54, 162, 235),   
        new RGBAColor(255, 205, 86),   
        new RGBAColor(75, 192, 192),   
        new RGBAColor(153, 102, 255),  
        new RGBAColor(255, 159, 64),   
        new RGBAColor(201, 203, 207),  
        new RGBAColor(50, 205, 50),    
        new RGBAColor(218, 112, 214),  
        new RGBAColor(255, 20, 147),   
        new RGBAColor(30, 144, 255),   
        new RGBAColor(255, 165, 0)     
    );
    
    @PostConstruct
    public void init() {
        try {
            createDepartmentPieModel();
            LOGGER.info("StatisticsBean initialized successfully");
        } catch (Exception e) {
            String errorMsg = "Error initializing StatisticsBean: " + e.getMessage();
            LOGGER.log(Level.SEVERE, errorMsg, e);
            
            
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Chart Error", "Unable to load department statistics"));
            
           
            createEmptyChart();
        }
    }
    
    private void createDepartmentPieModel() {
        List<String> departmentLabels = new ArrayList<>();
        List<BigDecimal> userCounts = new ArrayList<>();
        
        // Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.severe("MySQL JDBC Driver not found: " + e.getMessage());
            createEmptyChart();
            return;
        }
        
       
        String sql = "SELECT department, COUNT(*) AS user_count " +
                    "FROM users " +
                    "WHERE department IS NOT NULL AND department != '' " +
                    "GROUP BY department " +
                    "ORDER BY user_count DESC, department ASC";
        
        try (Connection conn = mysqlConnector.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Database connection is null!");
                createEmptyChart();
                return;
            }
            
            LOGGER.info("Database connection established successfully");
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                
                while (rs.next()) {
                    String department = rs.getString("department");
                    int count = rs.getInt("user_count");
                    
                    if (department != null && !department.trim().isEmpty() && count > 0) {
                        departmentLabels.add(department.trim());
                        userCounts.add(BigDecimal.valueOf(count));
                    }
                }
                
                // Check if we have data
                if (departmentLabels.isEmpty()) {
                    LOGGER.warning("No department data found in database");
                    createEmptyChart();
                    return;
                }
                
                // Create the pie chart
                createPieChart(departmentLabels, userCounts);
                
                LOGGER.info("Department pie chart created successfully with " + 
                           departmentLabels.size() + " departments");
                
            }
        } catch (SQLException e) {
            String errorMsg = "Database error fetching department statistics: " + e.getMessage();
            LOGGER.log(Level.SEVERE, errorMsg, e);
            createEmptyChart();
            
           
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, 
                "Data Error", "Unable to fetch latest department data"));
        }
    }
    
    private void createPieChart(List<String> labels, List<BigDecimal> data) {
 
        List<RGBAColor> chartColors = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            chartColors.add(CHART_COLORS.get(i % CHART_COLORS.size()));
        }
        
        departmentPieModel = new PieChart()
            .setData(new PieData()
                .addDataset(new PieDataset()
                    .setData(data.toArray(new BigDecimal[0]))
                    .setLabel("Users by Department")
                    .addBackgroundColors(chartColors.toArray(new RGBAColor[0]))
                    .setBorderWidth(2)
                    .setBorderColor(new RGBAColor(255, 255, 255)) 
                )
                .setLabels(labels.toArray(new String[0]))
            )
            .setOptions(new PieOptions()
                .setResponsive(true)
                .setMaintainAspectRatio(false)
                .setPlugins(new Plugins()
                    .setTitle(new Title()
                        .setDisplay(true)
                        .setText("Department Distribution"))
                )
            )
            .toJson();
    }
    
    private void createEmptyChart() {
        departmentPieModel = new PieChart()
            .setData(new PieData()
                .addDataset(new PieDataset()
                    .setData(BigDecimal.ONE)
                    .setLabel("No Data Available")
                    .addBackgroundColors(new RGBAColor(200, 200, 200))
                )
                .setLabels("No Data")
            )
            .setOptions(new PieOptions()
                .setResponsive(true)
                .setMaintainAspectRatio(false)
                .setPlugins(new Plugins()
                    .setTitle(new Title()
                        .setDisplay(true)
                        .setText("Department Distribution - No Data Available"))
                )
            )
            .toJson();
    }
    
    
    public void itemSelect(ItemSelectEvent event) {
        if (event.getData() != null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, 
                "Department Selected",
                "Value: " + event.getData() + 
                ", Item Index: " + event.getItemIndex() + 
                ", DataSet Index: " + event.getDataSetIndex());
            
            FacesContext.getCurrentInstance().addMessage(null, msg);
            LOGGER.info("Chart item selected: " + event.getData());
        }
    }
    
    
    public void refreshChart() {
        try {
            createDepartmentPieModel();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, 
                "Success", "Chart data refreshed"));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error refreshing chart", e);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "Failed to refresh chart data"));
        }
    }
    
    
    public String getDebugMessage() {
        return "StatisticsBean is accessible! Chart model is " + 
               (departmentPieModel != null ? "loaded" : "null");
    }
    
    
    public String getDepartmentPieModel() {
        if (departmentPieModel == null) {
            LOGGER.warning("departmentPieModel is null, returning empty chart");
            createEmptyChart();
        }
        return departmentPieModel;
    }
    
    public void setDepartmentPieModel(String departmentPieModel) {
        this.departmentPieModel = departmentPieModel;
    }
}