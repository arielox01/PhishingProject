
<?php
require('../includes/DbOperations.php');
$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
	if(
		isset($_POST['username']) and
		isset($_POST['myemail']) and
		isset($_POST['password']) and 
		isset($_POST['otheremail']) ){
			
		$db = new DbOperations();
		
		$result = $db->createEmailForUser($_POST['username'], $_POST['password'], $_POST['myemail'], $_POST['otheremail']);
		if ($result == 1){
			$response['error'] = false;
			$response['message'] = "Email added successfully";
		} else if ($result == 2){
			$response['error'] = true;
			$response['message'] = "Some error occured please try again";
		} else if ($result == 0){
			$response['error'] = true;
			$response['message'] = "It seens you are not registered";
		}
	} else{
		$response['error'] = true;
		$response['message'] = "Required fields are missing";
	}
}
echo json_encode($response);
?>