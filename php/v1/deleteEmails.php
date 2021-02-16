<?php

require('../includes/DbOperations.php');
$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
	if(isset($_POST['email']) ){
		
		$db = new DbOperations();
		$db->deleteTableByEmail($_POST['email']);
		
		$response['error'] = false;
		$response['message'] = "The action was successfully";
		
	} else {
		$response['error'] = true;
		$response['message'] = "Required fields are missing";
	}
}
echo json_encode($response);
?>