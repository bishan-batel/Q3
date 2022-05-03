<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<!--
Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Html.html to edit this template
-->
<html>
    <head>
        <title>TODO supply a title</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" type="text/css" href="css/account.css"/>
        <script defer src="js/libs/jquery/jquery.min.js"></script>
        <script defer src="js/global.js"></script>
        <script defer src="js/account.js"></script>

    </head>
    <body>
        <div id="main-bg">
        </div>
        <main class="reveal">
            <div id="bg"></div>


            <form id="authform" method="post">
                <label class="login-field reveal">
                    Email
                    <input type="email"/>
                </label>

                <label class="login-field reveal">
                    Password
                    <input type="password"/>
                </label>
                <div id="submission" class="reveal">
                    <input type="submit" value="Login" />
                    <input type="submit" value="Register"/>
                </div>
            </form>
        </main>
    </body>
</html>
