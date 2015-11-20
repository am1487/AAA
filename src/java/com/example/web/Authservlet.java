/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Authservlet extends HttpServlet {

    //DATABASE DETAILS
    private static String DB="jdbc:mysql://localhost:3306/www";
    private static String DBUSER="www";
    private static String DBPSW="12345";
   
    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        

        //connect to database
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB, DBUSER, DBPSW);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Authservlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Authservlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        String user = request.getParameter("uname");
        String pass = request.getParameter("pass");

        // Set up ldap environment
        Hashtable env = new Hashtable(11);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://ldap.uth.gr:389/");
        env.put(Context.SECURITY_PRINCIPAL, "uid=" + user + ", ou=People, dc=uth,dc=gr");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_CREDENTIALS, pass);

        PrintWriter out = response.getWriter();

        try {
            DirContext ctx = new InitialDirContext(env);
            System.out.println(ctx.getEnvironment());

            stmt = conn.createStatement();
            
            //db query for current user 
            rs = stmt.executeQuery("SELECT * FROM profile WHERE username='" + user + "'");
            
           
            HttpSession session;
            
            if (rs.next()) {
                int role = rs.getInt("Role");
                rs.beforeFirst();
                if (role != 0) {

                    // Login succesful, start session

                    session = request.getSession();
                    session.setAttribute("user", user);
                    //setting session to expiry in 30 mins
                    session.setMaxInactiveInterval(60*60);
                    Cookie userName = new Cookie("user", user);
                    userName.setMaxAge(60*60);
                    response.addCookie(userName);

                    if (rs.next()) {
                        String id = rs.getString("ID");
                        String username = rs.getString("username");
                        int accounting = rs.getInt("accounting");
                        accounting++;
                        //out.println("ID: " + id + " Login Counter: " + accounting + " Username: " + username);
                        PreparedStatement pstmt = conn.prepareStatement("UPDATE profile SET Accounting=? WHERE ID=?");
                        pstmt.setInt(1, accounting);
                        pstmt.setString(2, id);
                        boolean result = pstmt.execute();
                        if (!result) { System.out.println("ERROR: Wrong Query"); }

                        RequestDispatcher view = request.getRequestDispatcher("listUsers.jsp");
                        view.forward(request, response);
                    } 

                } else {
                    out.println("You are a blocked user on our site!");
                    out.println("Please contact an administrator, to be able to login again!");
                }
            }
            else {
                        //If there are no data insert this user into the database...
                        //N/A yet
                        out.println("Adding user to database...");
                        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO profile(Role, Login_type, Accounting, username) VALUES (?,?,?,?)");
                        /*Available Roles
                            - 0 => Disabled User    (User that we don't count his login or block him on our site neither backend nor frontend).
                            - 1 => Simple User      (User that has only minor privilleges, only frontend).
                            - 2 => Administrator    (User that has full access to the backend & frontend).
                        */
                        pstmt.setInt(1, 1); //Default role for users
                        pstmt.setInt(2, 0); //Login_type = 0 for users from uth
                        pstmt.setInt(3, 1); //The number of times he has logged in
                        pstmt.setString(4, user); //User's username
                        boolean result = pstmt.execute();

                        if (!result) { System.out.println("ERROR: Wrong Query"); }
                        else { out.println("User " + user + " was added successfully"); }
                        
                    session = request.getSession();
                    session.setAttribute("user", user);
                    //setting session to expiry in 30 mins
                    session.setMaxInactiveInterval(60*60);
                    Cookie userName = new Cookie("user", user);
                    userName.setMaxAge(60*60);
                    response.addCookie(userName);
                    
                    rs = stmt.executeQuery("SELECT * FROM profile WHERE username='" + user + "'");

                    if (rs.next()) {
                        String id = rs.getString("ID");
                        String username = rs.getString("username");
                        int accounting = rs.getInt("accounting");
                        accounting++;
                        //out.println("ID: " + id + " Login Counter: " + accounting + " Username: " + username);
                        pstmt = conn.prepareStatement("UPDATE profile SET Accounting=? WHERE ID=?");
                        pstmt.setInt(1, accounting);
                        pstmt.setString(2, id);
                        boolean result2 = pstmt.execute();
                        if (!result2) { System.out.println("ERROR: Wrong Query"); }

                        RequestDispatcher view = request.getRequestDispatcher("listUsers.jsp");
                        view.forward(request, response);
                    } 

                    }
            out.close();
            ctx.close();            
        } catch (AuthenticationNotSupportedException ex) {
            out.println("<html><body>Authetication not supported</body></html>");
        } catch (AuthenticationException ex) {
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
            out.println("<html><body>Incorrect password</body></html>");
            rd.include(request, response);  
        } catch (NamingException ex) {
            out.println("<html><body>Error new context</body></html>");
        } catch (NullPointerException ex) {
            out.println("<html>NULL Pointer</html>");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
