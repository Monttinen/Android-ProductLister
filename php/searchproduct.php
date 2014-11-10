<?php

require_once 'DB_config.php';

$db = new PDO("mysql:dbname=$dbname;host=$dbhost;charset=utf8", $dbuser, $dbpass);

$response = array();

if(isset($_GET['keyword'])){
    $k = "%$_GET[keyword]%";

    $ps = $db->prepare("SELECT * FROM Product WHERE ProductName LIKE ?");
    
    $success = $ps->execute(array($k));
    
    $response["Success"] = $success;
    
    if($success){
        $response["Products"] = array();
        
        while($row = $ps->fetch(PDO::FETCH_ASSOC)){
            array_push($response["Products"], $row);
        }
    } else {
        $response["Message"] = "Database query failed.";
    }
} else {
    $response["Success"] = 0;
    $response["Message"] = "Required field(s) is missing";
}

echo json_encode($response);