<?php

/**
 * Validates parameters.
 * @author Jörg Quick
 * @version 1.0
 */

/* Validate parameters */
function validate($data) {
    $data = trim($data);
    $data = stripcslashes($data);
    $data = htmlspecialchars($data);
    return $data;
}
?>