<?php

error_reporting(-1);

require('../includes/DbOperations.php');
$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
				
		$emails = array();
		$emails_id = array();
		for ($i = 0; $i <= $_POST['numOfEmails']; $i++) {
            if (!empty($_POST['emails_id_'.$i]) and !empty($_POST['emails_'.$i])){
                $emails_id[] = $_POST['emails_id_'.$i];
                $emails[] = $_POST['emails_'.$i];
                echo "added\n";
            }
        }
		$from = "test@hostinger-tutorials.com";
        $headers = "From:" . $from;
        $table = $_POST['table'];
        $index = 0;
        foreach($emails as $contact) {
            $email_id = $emails_id[$index];
            $index++;
            $to      =  $contact;
            $subject = 'Click The Link Below!!!';
            $message = "Are you interested in 1 million usd???? Click The Link: https://7cibus.com/projectbiuni/v1/index.php?email=";
            $message .= $table;
            $message .= "&email_id=";
            $message .= $email_id;
            mail($to, $subject, $message, $headers);
            echo "The email message was sent to ".$to."\n";
        }
		$response['error'] = false;
		$response['message'] = "The action was successfully";
}
echo json_encode($response);
?>