<%@ page import="java.io.*,java.util.*,java.sql.*"%>
<%@ page import="javax.servlet.http.*,javax.servlet.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>User AAA - List Users</title>

        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" integrity="sha512-dTfge/zgoMYpP7QbHy4gWMEGsbsdZeCXz7irItjcC3sPUFtf0kuFbDz/ixG7ArTxmDjLXDmezHubeNikyKGVyQ==" crossorigin="anonymous">
        <link href="css/main.css" rel="stylesheet" type="text/css"/>

        <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
          <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
          <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>
    <body>
        <%
            //allow access only if session exists
            String user = null;
            if (session.getAttribute("user") == null) {
                response.sendRedirect("index.jsp");
            } else {
                user = (String) session.getAttribute("user");
            }
            String userName = null;
            String sessionID = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("user")) {
                        userName = cookie.getValue();
                    }
                    if (cookie.getName().equals("JSESSIONID")) {
                        sessionID = cookie.getValue();
                    }
                }
            }
        %>
        <sql:setDataSource var="snapshot" driver="com.mysql.jdbc.Driver"
                           url="jdbc:mysql://localhost/www"
                           user="www"  password="12345"/>

        <sql:query dataSource="${snapshot}" var="result">
            SELECT * from profile;
        </sql:query>

        <div class="row">
            <div class="buttons col-md-9">
                <div class="pull-right">
                    <button class="btn btn-primary" type="button">
                        Welcome, <%=user%>
                    </button>

                    <form action="Logout" method="post">
                        <input  id="logoutid" class="btn btn-primary" type="submit" value="Logout" >
                    </form>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="main col-md-6 center-block">

                <h2> User's List </h2>

                <table class="table table-hover table-bordered">
                    <tr>
                        <th>ID</th>
                        <th>Role</th>
                        <th>Login Type</th>
                        <th>Accounting</th>
                        <th>Username</th>
                        <th>Submit</th>
                    </tr>
                            <c:forEach var="row" items="${result.rows}">
                                <form method='post' action='update_db'>
                                    <tr>
                                        <td><input type="hidden" value="${row.ID}" name="id"><c:out value="${row.ID}"/></td>
                                        <td>
                                            <div class="dropdown">
                                                <!--<button class="btn btn-default" id="dLabel" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                    <%--<c:out value="${row.Role}"/>--%>
                                                    <span class="caret"></span>
                                                </button>-->
                                                <select name="update_role">
                                                    <option value="0" ${row.Role == 0 ? 'selected' : ''}>Disabled</option>
                                                    <option value="1" ${row.Role == 1 ? 'selected' : ''}>User</option>
                                                    <option value="2" ${row.Role == 2 ? 'selected' : ''}>Administrator</option>
                                                </select>
                                                <!--<ul class="dropdown-menu" aria-labelledby="dLabel">
                                                    <li><a href="#">0</a></li>
                                                    <li><a href="#">1</a></li>
                                                    <li><a href="#">2</a></li>
                                                </ul>-->
                                            </div>
                                        </td>
                                        <td><c:out value="${row.Login_type}"/></td>
                                        <td><c:out value="${row.Accounting}"/></td>
                                        <td><c:out value="${row.username}"/></td>
                                        <td><center>
                                            <input class="btn btn-default" id="dLabel" style="background:#337ab7; color:white" type="submit" value="Submit" name="change_role" />
                                        </center></td>
                                    </tr>
                                </form>
                            </c:forEach>
                     
                </table>
            </div>
        </div>
    </div>

    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js" integrity="sha512-K1qjQ+NcF2TYO/eI3M6v8EiNYZfA95pQumfvcVrTHtwQVDG+aHRqLi/ETn2uB+1JqwYqVG3LIvdm9lj6imS/pQ==" crossorigin="anonymous"></script>
</body>
</html>
