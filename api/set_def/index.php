<?php

/**
 * Handle set request.
 * @author Jörg Quick
 * @version 1.0
 */

header('Content-type: application/json');
require_once "../functions/conn.php";
require_once "../functions/functions.php";

if (isset($_GET['access_token']) && isset($_GET['station_id']) && isset($_GET['report_msg'])) {
    $access_token = validate($_GET['access_token']);
    $station_id = validate($_GET['station_id']);
    $report_msg = validate($_GET['report_msg']);

    $access = "SELECT * FROM tokens WHERE token = '$access_token'";
    $grandted = $conn->query($access);

    if ($grandted->num_rows == 0) {
        $json_array = array('response_code' => 69, 
        'response_msg' => "forbidden");
        echo json_encode($json_array, JSON_PRETTY_PRINT);
    } else {
        $sql = "INSERT IGNORE INTO defect_stations (station_id, report_msg) VALUES ('$station_id', '$report_msg')";
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
} else {
    $json_array = array('response_code' => 2, 
    'response_msg' => "wrong parameters");
    echo json_encode($json_array, JSON_PRETTY_PRINT);
}

$conn->close();
?>