
<?php
require('../includes/DbOperations.php');
$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
	if(
		isset($_POST['email']) ){
			
		$db = new DbOperations();
		
		$response['emailsArray'] = $db->getEmailsByEmail($_POST['email']);
		$response['error'] = false;
		$response['message'] = "The action was successfully";
		$response['numOfEmails'] = $db->getNumOfEmailsByEmail($_POST['email']);
		
	} else{
		$response['error'] = true;
		$response['message'] = "Required fields are missing";
	}
}
echo json_encode($response);
?>