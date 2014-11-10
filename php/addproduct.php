<?php

require_once 'DB_config.php';

$db = new PDO("mysql:dbname=$dbname;host=$dbhost;charset=utf8", $dbuser, $dbpass);

$response = array();

$imageDir = './img/';

// ProductName
if(isset($_POST['ProductName']) && is_string($_POST['ProductName'])){
	$ProductName = $_POST['ProductName'];
} else {
	throw new Exception("ProdcutName is not a string");
}

// ProductCategoryId
if(isset($_POST['ProductCategoryId']) && is_int($_POST['ProductCategoryId'])){
	$ProductCategoryId = $_POST['ProductCategoryId'];
} else {
	throw new Exception("ProdcutCategoryId is not an integer");
}

// ProductBarcode
if(isset($_POST['ProductBarcode'])){
	$ProductBarcode = $_POST['ProductBarcode'];
} else {
	$ProductBarcode = null;
}

// Image
if(isset($_POST['Image']) && is_string($_POST['Image'])){
	$Image = $_POST['Image'];
}

// TODO send to database and get id for storing image