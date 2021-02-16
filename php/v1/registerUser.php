
<?php

require('../includes/DbOperations.php');
$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
	
	if(
		isset($_POST['username']) and
			isset($_POST['email']) and
				isset($_POST['password']))
		
		{
		//operate the data further
		
		$db = new DbOperations();
		
		$result = $db->createUser(
			$_POST['username'],
			$_POST['password'],
			$_POST['email']);
		if($result == 1){
			$db->createTableForUser(
			$_POST['username'],
			$_POST['password'],
			$_POST['email']);
			$response['error'] = false;
			$response['message'] = "User registered successfully";
		}else if ($result == 2){
			$response['error'] = true;
			$response['message'] = "Some error occured please try again";
		} else if ($result == 0){
			$response['error'] = true;
			$response['message'] = "It seens you are already registered, please choose a different email and username";
		}
		
		
	} else{
		$response['error'] = true;
		$response['message'] = "Required fields are missing";
	}
	
}else{
	$response['error'] = true;
	$response['message'] = "Invalid Request";
}

echo json_encode($response);
?>