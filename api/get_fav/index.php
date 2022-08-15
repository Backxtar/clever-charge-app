<?php

/**
 * Handle login request.
 * @author Jörg Quick
 * @version 1.0
 */

header('Content-type: application/json');
require_once "../functions/conn.php";
require_once "../functions/functions.php";

if (isset($_GET['access_token']) && isset($_GET['userid'])) {
    $access_token = validate($_GET['access_token']);
    $userid = validate($_GET['userid']);

    $access = "SELECT * FROM tokens WHERE token = '$access_token'";
    $grandted = $conn->query($access);

    if ($grandted->num_rows == 0) {
        $json_array = array('response_code' => 69, 'response_msg' => "forbidden");
        echo json_encode($json_array, JSON_PRETTY_PRINT);
    } else {
        if (!is_numeric($userid)) {
            $json_array = array('response_code' => 2, 'response_msg' => "wrong parameters");
            echo json_encode($json_array, JSON_PRETTY_PRINT);
        } else {
            $sql = "SELECT * FROM user_favorites WHERE user_id = $userid";
            $result = $conn->query($sql);
            $json_ids = array();

            if ($result->num_rows > 0) {

                foreach ($result as $row) {
                    $json_ids[] = (int)$row['station_id'];
                }
            }
            $json_array = array('response_code' => 1, 'favorites' => $result->num_rows, 'fav_station_ids' => $json_ids);
            echo json_encode($json_array, JSON_PRETTY_PRINT);
        }
    }
} else {
    $json_array = array('response_code' => 2, 
    'response_msg' => "wrong parameters");
    echo json_encode($json_array, JSON_PRETTY_PRINT);
}
$conn->close();
?>