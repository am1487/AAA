package com.example.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class updateDatabase extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
               
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/www", "www", "12345");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Authservlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Authservlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String uRole = request.getParameter("update_role");
        String myID = request.getParameter("id");
        //DEBUGGING!!!
        PrintWriter out = response.getWriter();
        //out.println(id + " " + value);
        
        try {
            stmt = conn.createStatement();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE profile SET Role=? WHERE ID=?");
            pstmt.setInt(1, Integer.parseInt(uRole));
            pstmt.setString(2, myID);
            boolean result = pstmt.execute();
            if (!result) { System.out.println("ERROR: Wrong Query"); }
            
            RequestDispatcher view = request.getRequestDispatcher("listUsers.jsp");
            view.forward(request, response);
        } catch (SQLException ex) {
            out.println("<html><body>Authetication not supported</body></html>");
        } catch (NullPointerException nex) {
            out.println("<html><body>NULL Pointer</body></html>");
        } 
    }
}
