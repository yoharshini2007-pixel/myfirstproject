<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - Marketplace</title>
</head>
<body>
    <h2>User Login</h2>
    <form action="login" method="post">
        <label>Email:</label><br>
        <input type="email" name="email" required><br><br>

        <label>Password:</label><br>
        <input type="password" name="password" required><br><br>

        <button type="submit">Login</button>
    </form>
    <br>
    <p>Don't have an account? <a href="register.jsp">Register here</a></p>
</body>
</html>