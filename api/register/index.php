<?php

/**
 * Handle register request.
 * @author Jörg Quick
 * @version 1.0
 */

header('Content-type: application/json');
require_once "../functions/conn.php";
require_once "../functions/functions.php";

if (isset($_GET['access_token']) && isset($_GET['username']) && isset($_GET['userpasswd']) && isset($_GET['useremail'])) {
    $access_token = validate($_GET['access_token']);
    $username = validate($_GET['username']);
    $userpasswd = validate($_GET['userpasswd']);
    $useremail = validate($_GET['useremail']);

    if (empty($access_token) || empty($username) || empty($userpasswd) || empty($useremail)) {
        $json_array = array('response_code' => 4, 
        'response_msg' => "empty parameters");
        echo json_encode($json_array, JSON_PRETTY_PRINT);
    } else {
        $access = "SELECT * FROM tokens WHERE token = '$access_token'";
        $grandted = $conn->query($access);

        if ($grandted->num_rows == 0) {
            $json_array = array('response_code' => 69, 
            'response_msg' => "forbidden");
            echo json_encode($json_array, JSON_PRETTY_PRINT);
        } else {
            $sql = "SELECT * FROM user_register WHERE user_name = '$username' OR user_email = '$useremail'";
            $result = $conn->query($sql);

            if ($result->num_rows > 0) {
                $json_array = array('response_code' => 3, 
                'response_msg' => "already exist");
                echo json_encode($json_array, JSON_PRETTY_PRINT);
            } else {
                $sql = "INSERT INTO user_register (user_name, user_passwd, user_email) VALUES ('$username', '" . md5($userpasswd) . "', '$useremail')";
                if (!$conn->query($sql)) {
                    $json_array = array('response_code' => 0, 
                    'response_msg' => "insert failed");
                    echo json_encode($json_array, JSON_PRETTY_PRINT);
                } else {
                    $json_array = array('response_code' => 1, 
                    'response_msg' => "insert success");
                    echo json_encode($json_array, JSON_PRETTY_PRINT);
                }
            }
        }
    }
} else {
    $json_array = array('response_code' => 2, 
    'response_msg' => "wrong parameters");
    echo json_encode($json_array, JSON_PRETTY_PRINT);
}

$conn->close();
?>