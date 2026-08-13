<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register - Marketplace</title>
</head>
<body>
    <h2>User Registration</h2>
    <form action="register" method="post">
        <label>Name:</label><br>
        <input type="text" name="name" required><br><br>

        <label>Email:</label><br>
        <input type="email" name="email" required><br><br>

        <label>Password:</label><br>
        <input type="password" name="password" required><br><br>

        <label>Role:</label><br>
        <select name="role">
            <option value="buyer">Buyer</option>
            <option value="seller">Seller</option>
        </select><br><br>

        <button type="submit">Register</button>
    </form>
    <br>
    <p>Already have an account? <a href="login.jsp">Login here</a></p>
</body>
</html>