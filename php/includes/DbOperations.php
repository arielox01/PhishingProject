<?php

	class DbOperations{
		
		private $con;
		
		function __construct(){
						
			$db = new DbConnect();
			
			$this->con = $db->connect();
			
		}
		
		public function deleteTableByEmail($email){
			mysqli_query($this->con, "DELETE FROM `".$email."`");
			return 0;
		}
		
		public function createUser($username, $pass, $email){
			
			if ($this->isUserExist($username, $pass)){
				return 0;
			}else{
				$password = md5($pass);
				$stmt = $this->con->prepare("INSERT INTO `users` (`id`, `username`, `password`, `email`, `type`) VALUES (NULL, ?, ?, ?, 0);");
				$stmt->bind_param("sss", $username, $password, $email);
				
				if($stmt->execute()){
					return 1;
				}else{
					return 2;
				}
			}
		}
		
		public function createEmailForUser($username, $pass, $myemail, $otheremail){
			$stmt = $this->con->prepare("INSERT INTO `".$myemail."` (`email_id`, `email`, `phished`) VALUES (NULL, ?, 0)");
			$stmt->bind_param("s", $otheremail);
			
			if($stmt->execute()){
				return 1;
			}else{
				return 2;
			}
		}
		
		public function createTableForUser($username, $pass, $email){	
			mysqli_query($this->con, "CREATE TABLE `".$email."` ( email_id INT(11) AUTO_INCREMENT PRIMARY KEY, email VARCHAR(100) NOT NULL, phished INT NOT NULL)") or die ("Bad Create table: $email");
		}
		
		public function userLogin($username, $pass){
			$password = md5($pass);
			$stmt = $this->con->prepare("SELECT id FROM users WHERE username = ? AND password = ?");
			$stmt->bind_param("ss", $username, $password);
			$stmt->execute();
			$stmt->store_result();
			return $stmt->num_rows > 0;
		}
		
		public function getUserByUsername($username){
			$stmt = $this->con->prepare("SELECT * FROM users WHERE username = ?");
			$stmt->bind_param("s", $username);
			$stmt->execute();
			return $stmt->get_result()->fetch_assoc();
		}
		
		public function getUserByEmail($email){
			$stmt = $this->con->prepare("SELECT * FROM users WHERE email = ?");
			$stmt->bind_param("s", $email);
			$stmt->execute();
			return $stmt->get_result()->fetch_assoc();
		}
		
		private function isUserExist($username, $email){
			$stmt = $this->con->prepare("SELECT id FROM users WHERE username = ? OR email = ?");
			$stmt->bind_param("ss", $username, $email);
			$stmt->execute();
			$stmt->store_result();
			return $stmt->num_rows > 0;
		}
		
		public function getEmailsByEmail($email){
			$response = array();
			if ($result = mysqli_query($this->con, "SELECT * FROM `".$email."`")){
				while($row = mysqli_fetch_array($result)){
					$response[] = array($row['email_id'], $row['email'], $row['phished']);
				}
			}
			
			return $response;
		}
		
		public function getUsersEmails(){
			$response = array();
			if ($result = mysqli_query($this->con, "SELECT * FROM `users`")){
				while($row = mysqli_fetch_array($result)){
					$response[] = array($row['id'], $row['username'], $row['password'], $row['email'], $row['type']);
				}
			}
			
			return $response;
		}
		
		public function getNumOfEmailsByEmail($email){
			$numOfEmails = 0;
			if ($result = mysqli_query($this->con, "SELECT * FROM `".$email."`")){
				while($row = mysqli_fetch_array($result)){
					$numOfEmails++;
				}
			}
			return $numOfEmails;
		}
		
		public function getNumOfUsersEmails(){
			$numOfEmails = 0;
			if ($result = mysqli_query($this->con, "SELECT * FROM `users`")){
				while($row = mysqli_fetch_array($result)){
					$numOfEmails++;
				}
			}
			return $numOfEmails;
		}
			
		public function updateUserPhished($email, $email_id){
			
			mysqli_query($this->con, "UPDATE `".$email."` SET `phished` = '1' WHERE `".$email."`.`email_id` = ".$email_id.";");
						
			return $email;
		}
		
		public function deleteUser($email, $id){
			
			mysqli_query($this->con, "DELETE FROM `users` WHERE `users`.`id` = ".$id.";");
			mysqli_query($this->con, "DROP TABLE `".$email."`;");
						
			return 0;
		}
	}
	
	class DbConnect{
		
		private $con;
		
		function __construct(){
			
		}
		
		function connect(){
			$this->con = new mysqli('localhost', 'u861647236_projectbiuni', '1906rajwanG', 'u861647236_projectbiuni');
			
			if(mysqli_connect_errno()){
				echo "Failed to connect to database".mysqli_connect_err();
			}
			
			return $this->con;
		}
	}
?>