<?php

/**
 * Handle set request.
 * @author Jörg Quick
 * @version 1.0
 */

header('Content-type: application/json');
require_once "../functions/conn.php";
require_once "../functions/functions.php";

if (isset($_GET['access_token'])) {
    $access_token = validate($_GET['access_token']);

    $access = "SELECT * FROM tokens WHERE token = '$access_token'";
    $grandted = $conn->query($access);

    if ($grandted->num_rows == 0) {
        $json_array = array('response_code' => 69, 
        'response_msg' => "forbidden");
        echo json_encode($json_array, JSON_PRETTY_PRINT);
    } else {
        $sql = "SELECT * FROM defect_stations";
        $result_stations = $conn->query($sql);
        $json_station_map = array();

        if ($result_stations->num_rows > 0) {
            foreach ($result_stations as $row) {
                $json_station_map[(int)$row['station_id']] = $row['report_msg'];
            }
        }

        $json_array = array('response_code' => 1, 
                            'response_msg' => "query successful", 
                            'defect_stations_map' => $json_station_map);

        echo json_encode($json_array, JSON_PRETTY_PRINT);
    }
} else {
    $json_array = array('response_code' => 2, 
    'response_msg' => "wrong parameters");
    echo json_encode($json_array, JSON_PRETTY_PRINT);
}

$conn->close();
?>