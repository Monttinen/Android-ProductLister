<?php

require_once 'DB_config.php';

$db = new PDO("mysql:dbname=$dbname;host=$dbhost;charset=utf8", $dbuser, $dbpass);

$response = array();

$imageDir = '/var/www/html/img/';

try {
	// ProductName
	if (isset($_REQUEST['ProductName']) && is_string($_REQUEST['ProductName']) && strlen($_REQUEST['ProductName']) > 1) {
		$ProductName = $_REQUEST['ProductName'];
	} else {
		throw new Exception("ProdcutName is not a string");
	}

	// ProductCategoryId
	if (isset($_REQUEST['ProductCategoryId']) && is_numeric($_REQUEST['ProductCategoryId'])) {
		$ProductCategoryId = $_REQUEST['ProductCategoryId'];
	} else {
		throw new Exception("ProdcutCategoryId is not an integer");
	}

	// ProductBarcode
	if (isset($_REQUEST['ProductBarcode'])) {
		$ProductBarcode = $_REQUEST['ProductBarcode'];
	} else {
		$ProductBarcode = null;
	}

	// Image
	if (isset($_FILE['Image']['name']) && is_string($_FILE['Image']['name'])) {
		$Image = $_FILE['Image'];
	} else {
		$Image = null;
	}
	
	$sql = "INSERT INTO Product (ProductName, ProductCategoryId, ProductBarcode) VALUES(?, ?, ?);";
	$ps = $db->prepare($sql);
	$result = $ps->execute(array($ProductName, $ProductCategoryId, $ProductBarcode));
	if($result == false){
		throw new Exception("Database query failed: ".$db->errorInfo());
	}
	
	$lastProductId = $db->lastInsertId();
	
	try {
		if(!is_uploaded_file($_Image['tmp_name'])){
			throw new Exception("File was not uploaded.");
		}
		if($Image['type'] != 'image/jpeg' || getimagesize($_Image['tmp_name'])==false){
			throw new Exception("File was not a jpeg file.");
		}
		if($Image['size'] > 5242880){
			throw new Exception("File was too large.");
		}
		
		$targetFileName = $imageDir.$lastProductId.".jpg";
		if(file_exists($targetFileName)){
			throw new Exception("File already exists.");
		}
		
		// Move the file to image folder
		move_uploaded_file($Image['tmp_name'], $targetFileName);
		
	} catch (Exception $ex) {
		throw $ex;
	}
	$response['Success'] = true;
} catch (Exception $ex) {
	$response['Success'] = false;
	$response['Message'] = $ex->getMessage();
}

echo json_encode($response);