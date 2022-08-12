<?php

/**
 * Creates a connection to database.
 * @author Jörg Quick
 * @version 1.0
 */

 /* Server data */
$server = "127.0.0.1";
$username = "username";
$password = "passwort";
$database = "database";

/* Connect to database */
$conn = new mysqli($server, $username, $password, $database);

/* If connection fails */
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
?>