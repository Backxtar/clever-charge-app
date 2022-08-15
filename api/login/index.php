<?php

/**
 * Handle login request.
 * @author Jörg Quick
 * @version 1.0
 */

header('Content-type: application/json');
require_once "../functions/conn.php";
require_once "../functions/functions.php";

if (isset($_GET['access_token']) && isset($_GET['username']) && isset($_GET['userpasswd'])) {
    $access_token = validate($_GET['access_token']);
    $username = validate($_GET['username']);
    $userpasswd = validate($_GET['userpasswd']);

    $access = "SELECT * FROM tokens WHERE token = '$access_token'";
    $grandted = $conn->query($access);

    if ($grandted->num_rows == 0) {
        $json_array = array('response_code' => 69, 
        'response_msg' => "forbidden");
        echo json_encode($json_array, JSON_PRETTY_PRINT);
    } else {
        $sql = "SELECT * FROM user_register WHERE user_name = '$username' AND user_passwd = '" . md5($userpasswd) . "'";
        $result_user = $conn->query($sql);

        if ($result_user->num_rows == 0) {
            $json_array = array('response_code' => 0, 
            'response_msg' => "login failed");
            echo json_encode($json_array, JSON_PRETTY_PRINT);
        } else {
            foreach ($result_user as $row) {
                $ids[] = $row['user_id'];
                $devs[] = $row['is_dev'];
                $email[] = $row['user_email'];
                $isDev = FALSE;

                if ((int)$devs[0] == 1) {
                    $isDev = TRUE;
                }

                $sql = "SELECT * FROM user_favorites WHERE user_id = '$ids[0]'";
                $result_fav = $conn->query($sql);
                $json_ids = array();

                if ($result_fav->num_rows > 0) {
                    foreach ($result_fav as $row) {
                        $json_ids[] = (int)$row['station_id'];
                    }
                }

                $sql = "SELECT * FROM defect_stations";
                $result_stations = $conn->query($sql);
                $json_station_map = array();

                if ($result_stations->num_rows > 0) {
                    foreach ($result_stations as $row) {
                        $json_station_map[(int)$row['station_id']] = $row['report_msg'];
                    }
                }

                $sql = "SELECT * FROM user_news WHERE user_id = '$ids[0]'";
                $result_news = $conn->query($sql);
                $news_ids = array();

                if ($result_news->num_rows > 0) {
                    foreach ($result_news as $row) {
                        $news_ids[] = (int)$row['news_id'];
                    }
                }

                $json_array = array('response_code' => 1, 
                'response_msg' => "login successful", 
                'user_id' => (int)$ids[0], 
                'user_name' => $username, 
                'user_email' => $email[0], 
                'is_dev' => $isDev, 
                'favorites' => $result_fav->num_rows,
                'fav_station_ids' => $json_ids,
                'defect_stations_map' => $json_station_map,
                'news_read_ids' => $news_ids);

                echo json_encode($json_array, JSON_PRETTY_PRINT);
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