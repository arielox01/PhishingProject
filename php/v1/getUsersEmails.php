
<?php
require('../includes/DbOperations.php');
$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
			
	$db = new DbOperations();
		
	$response['emailsArray'] = $db->getUsersEmails();
	$response['error'] = false;
	$response['message'] = "The action was successfully";
	$response['numOfEmails'] = $db->getNumOfUsersEmails();

}
echo json_encode($response);
?>