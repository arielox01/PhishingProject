
<?php
header("Location: https://he.wikipedia.org/wiki/%D7%93%D7%99%D7%95%D7%92");

require('../includes/DbOperations.php');
$response = array();

$db = new DbOperations();

$response['username'] = $db->updateUserPhished($_GET['email'], $_GET['email_id']);

echo json_encode($response);
		
?>