<?php

require_once 'DB_config.php';

$db = new PDO("mysql:dbname=$dbname;host=$dbhost;charset=utf8", $dbuser, $dbpass);

$response = array();

if(isset($_GET['c'])){
	$c = $_GET['c'];
	$sql = "SELECT * FROM Category WHERE CategoryParentId = ?;";
	$ps = $db->prepare($sql);
	$ps->bindParam(1, $c, PDO::PARAM_INT);
} else {
	$sql = "SELECT * FROM Category WHERE CategoryParentId IS NULL;";
	$ps = $db->prepare($sql);
}


$success = $ps->execute();

$response["Success"] = $success;

if($success){
	$response["Categories"] = array();

	while($row = $ps->fetch(PDO::FETCH_ASSOC)){
		array_push($response["Categories"], $row);
	}
} else {
	$response["Message"] = "Database query failed.";
}

echo json_encode($response);
