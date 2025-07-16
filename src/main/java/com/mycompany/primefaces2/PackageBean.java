/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Named("packageBean")
@RequestScoped
public class PackageBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<InternetPackage> packages;

    @PostConstruct
    public void init() {
        packages = new ArrayList<>();
        loadInternetPackages();
    }

    private void loadInternetPackages() {
        try (Connection conn = mysqlConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT package_id, package_name, speed_mbps, price, billing_cycle, image_path, description FROM internet_packages ORDER BY speed_mbps ASC")) {

            while (rs.next()) {
                InternetPackage pkg = new InternetPackage();
                pkg.setPackageID(rs.getInt("package_id"));
                pkg.setName(rs.getString("package_name"));
                pkg.setSpeedMbps(rs.getInt("speed_mbps"));
                pkg.setPrice(rs.getDouble("price"));
                pkg.setBillingCycle(rs.getString("billing_cycle"));
                pkg.setDescription(rs.getString("description"));
                pkg.setImagePath(rs.getString("image_path"));

                packages.add(pkg);
            }
        } catch (SQLException e) {
            System.err.println("Database error loading internet packages: " + e.getMessage());
        }
    }

    public List<InternetPackage> getPackages() {
        return packages;
    }

    public static class InternetPackage implements Serializable {

        private int packageID;
        private String name;
        private int speedMbps;
        private double price;
        private String billingCycle;
        private String description;
        private String imagePath;

        public int getPackageID() {
            return packageID;
        }

        public String getName() {
            return name;
        }

        public int getSpeedMbps() {
            return speedMbps;
        }

        public double getPrice() {
            return price;
        }

        public String getBillingCycle() {
            return billingCycle;
        }

        public String getDescription() {
            return description;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setPackageID(int packageID) {
            this.packageID = packageID;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSpeedMbps(int speedMbps) {
            this.speedMbps = speedMbps;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public void setBillingCycle(String billingCycle) {
            this.billingCycle = billingCycle;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }
    }
}
