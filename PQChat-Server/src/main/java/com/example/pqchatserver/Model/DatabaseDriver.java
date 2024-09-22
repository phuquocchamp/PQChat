package com.example.pqchatserver.Model;

import org.json.JSONObject;

import java.io.*;
import java.sql.*;
import java.util.Base64;
import java.util.UUID;

public class DatabaseDriver {
    private Connection connection;
    public DatabaseDriver(){
        try{
            this.connection = DriverManager.getConnection("jdbc:sqlite:database.db");

        }catch (Exception e){
            System.out.println("[LOG] >>> Database Connection Failed");
        }
    }



    // ----------------------------- Evaluate Account ------------------------------------- //
    public JSONObject evaluatedAccount(String email, String password){
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        JSONObject successObject = new JSONObject();
        try{
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String clientID = resultSet.getString("id");
                String username = resultSet.getString("username");
                String fullname = resultSet.getString("fullname");
                InputStream avatarStream = resultSet.getBinaryStream("avatar");
                String avatarBase64 = "";
                if (avatarStream != null) {
                    byte[] imageBytes = avatarStream.readAllBytes();
                    avatarBase64 = Base64.getEncoder().encodeToString(imageBytes);
                }

                successObject.put("clientID", clientID);
                successObject.put("username", username);
                successObject.put("fullname", fullname);
                successObject.put("avatar", avatarBase64);
                successObject.put("flag", "success");
                return successObject;
            }
        } catch (SQLException e) {
            System.out.println("[LOG] >>> Error at evaluatedAccount function!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject failedObject = new JSONObject();
        failedObject.put("prefix", "evaluateAccount");
        failedObject.put("flag", "failed");
        return failedObject;
    }
    // ----------------------------- Create Account ------------------------------------- //

    public void createUser(String fullname, String username, String password){
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        // Path From content root java (intelij project)
        String path = "src/main/resources/Images/avatar-default.jpg";
        try{
            String sql = "INSERT INTO users (id, username, password, avatar, fullname) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, UUID.randomUUID().toString());
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setString(5, fullname);

            // đọc file từ path và lưu vào image data
            // preparedStatement.setString(4, iamge_data);
            File imageFile = new File(path);
            FileInputStream fis = new FileInputStream(imageFile);
            preparedStatement.setBinaryStream(4, fis, (int) imageFile.length());
            preparedStatement.executeUpdate();
            fis.close();
        } catch (SQLException e) {
            System.out.println("[LOG] >>> Error at evaluatedAccount function!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateUserPassword(String username, String newPassword) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String sql = "UPDATE users SET password = ? WHERE username = ?";
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[LOG] >>> Error updating password for user: " + username);
        }
        return false;
    }


    // ---------------------------- Fetch User By Username ---------------------------------//
    public boolean fetchUserByUsername(String username){
        ResultSet resultSet = null;
        try{
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
        }catch (SQLException e){
            System.out.println("[LOG] >>> Error at Fetch User By Username function!");
        }
        return false;
    }
}
