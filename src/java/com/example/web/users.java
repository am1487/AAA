package com.example.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class users extends HttpServlet{
    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String username;
        int id, role, login_type, accounting;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/www", "www", "12345");
        } catch (ClassNotFoundException ex) {
                Logger.getLogger(Authservlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
                Logger.getLogger(Authservlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        PrintWriter out = response.getWriter();
        RequestDispatcher view;
        
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM profile");
            if (rs.next()) {
                //Get data from the database
                id = rs.getInt("ID");
                role = rs.getInt("Role");
                login_type = rs.getInt("Login_type");
                accounting = rs.getInt("Accounting");
                username = rs.getString("username");
                //Logika edw ginetai h syndesh me to .jsp
                ArrayList userList = new ArrayList();
                userList.add(id);
                userList.add(role);
                userList.add(login_type);
                userList.add(accounting);
                userList.add(username);
                request.setAttribute("uList", userList);
                //System.out.println(">>>>>>>>>>>>>>>>>>>MALAKIA PRINT<<<<<<<<<<<<<<<<<<<<<<<<<<");
                view = request.getRequestDispatcher("listUsers.jsp");
                view.forward(request, response);
            } else {
                out.println("No data!!!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}