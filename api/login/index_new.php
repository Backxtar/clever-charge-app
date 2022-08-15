<?php
/**
 * Handle login request.
 * @author Jörg Quick
 * @version 1.0
 */

header('Content-type: application/json; charst=UTF-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');

require_once "functions/functions.php";

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents('php://input'));
    
    if (!empty($data->key)) {
        if ($data->key == 'abc') {
            $json = file_get_contents('data/dclone_events.json');
            $json_dc = json_decode($json, true);
            echo json_encode($json_dc, JSON_PRETTY_PRINT);
        } else {
            http_response_code(3);
            echo json_encode(array(
            'status' => 0,
            'message' => 'Wrong key!'
        ), JSON_PRETTY_PRINT);
        }
    } else {
        http_response_code(2);
        echo json_encode(array(
            'status' => 0,
            'message' => 'Missing key!'
        ), JSON_PRETTY_PRINT);
    }
} else {
    http_response_code(1);
    echo json_encode(array(
        'status' => 0,
        'message' => 'Access denied!'
    ), JSON_PRETTY_PRINT);
}
?>