<?php

 class DB_functions {

     private $db;

     function __construct() {            //constructor
         require_once 'DB_Connect.php';
         $this->db = new DB_Connect();   // connecting to database
         $this->db->connect();
     }

     function __destruct() {  }  // destructor

     /**
      * Check user if existed or not
      */
     public function isUserExisted($name) {
         $result = mysql_query("SELECT user from user_table WHERE name = '$name'");
         $no_of_rows = mysql_num_rows($result);
         if ($no_of_rows > 0) {
             // user existed
             return true;
         } else {
             // user not existed
             return false;
         }
     }

     public function getUserByNameAndPassword($name, $sub_password) {
         $result = mysql_query("SELECT * FROM user_table WHERE name = '$name'") or die(mysql_error());
         // check for result
         $no_of_rows = mysql_num_rows($result);
         if ($no_of_rows > 0) {
             $result = mysql_fetch_array($result);
             /*                                            //TODO: how to encrypt password?
             $salt = $result['salt'];
             $encrypted_password = $result['encrypted_password'];
             $hash = $this->checkhashSSHA($salt, $password);
             // check for password equality
             if ($encrypted_password == $hash) {
                 // user authentication details are correct
                 return $result;
             }*/
             $password = $result['password'];
             if ($sub_password == $password) {
                 // user authentication details are correct
                 return $result;
         } else {
             // user not found
             return false;
         }
       }
     }
	 
	 public function getClients(){
		 $query = "Select * FROM clients_table";
		 $q=mysql_query($query);
		 $output = array();
		 while($e = mysql_fetch_assoc($q))
				$output[]=$e;
			
		 return json_encode($output);
		 
	 }
	 
	  public function getCausals(){
		 $query = "Select * FROM causals_table";
		 $q=mysql_query($query);
		 $output = array();
		 while($e = mysql_fetch_assoc($q))
				$output[]=$e;
			
		 return json_encode($output);
		 
	 }
	 
	 public function insertEvent($id,$user_id,$eventDate,$eventTime,$client,$office, $causal, $note)
	 {
		$query = "INSERT INTO event_table (ID,user_id,date,time,client,office,causal,note) VALUES(".$id .",'". $user_id ."','". $eventDate ."','". $eventTime ."','". $client ."','". ($office == 'true'?1:0) ."','". $causal ."','". $note ."');";
        
        $q = mysql_query($query);

		return 'true';

	 }
	 
	 public function updateEvent($id,$user_id,$eventDate,$eventTime,$client,$office, $causal, $note)
	 {
		$query = "UPDATE event_table SET user_id = '". $user_id ."', date = '". $eventDate ."', time = '". $eventTime ."', client = '". $client ."', office = '".($office == 'true'?1:0)."', causal = '". $causal ."', note = '". $note ."' where ID = ". $id; 
        
        $q = mysql_query($query);
		
		return 'true';
		 
	 }
	 
	 public function deleteEvent($id)
	 {
		 $query = "DELETE FROM event_table WHERE ID = ". $id;
		 
		 $q = mysql_query($query);
		 
		 return 'true';
		 
		 
	 }
	 
	 
	 public function getEvents($d1,$d2){
		 $query = "Select event_table.*, clients_table.*, causals_table.* FROM event_table INNER JOIN clients_table ON client_id = event_table.client INNER JOIN causals_table ON causals_table.causal_id = event_table.causal where date >= '". $d1 ."' and date <= '". $d2 ."'";
		 
		 $q=mysql_query($query);
		
		 $output = array();
		 while($e = mysql_fetch_assoc($q))
				$output[]=$e;
				
		 return json_encode($output);
		 
	 }
	 
	 public function getEventByID($id)
	 {
		 $query = "Select * FROM event_table where ID = ". $id;
		 $q=mysql_query($query);
		 $output = array();
		 while($e = mysql_fetch_assoc($q))
				$output[]=$e;
			
		 return json_encode($output); 
	 }
	 
	 public function getLastEventID()
	 {
		 $query = "Select ID FROM event_table order by ID desc limit 1";
		 $q=mysql_query($query);
		  $output = array();
		 
		 while($e = mysql_fetch_assoc($q))
				$output[]=$e;
		
				
		 return json_encode($output); 
		 
	 }
	 
	  public function getSearch($title){  //insert here $vars to be used and passed via index.php and string[] parname and string[] par in getjson string
           
		  $query = "Select event_table.*, clients_table.*, causals_table.* FROM event_table INNER JOIN clients_table ON client_id = event_table.client INNER JOIN causals_table ON causals_table.causal_id = event_table.causal where clients_table.name = '". $title ."'";
		 
		 $q=mysql_query($query);

		 while($e = mysql_fetch_assoc($q))
				$output[]=$e;	
        
		 return json_encode($output);		 
	 }
	 
	  public function getMyEvents($user_id){
		 $query = "Select event_table.*, clients_table.*, causals_table.* FROM event_table INNER JOIN clients_table ON client_id = event_table.client INNER JOIN causals_table ON causals_table.causal_id = event_table.causal where event_table.user_id = '". $user_id."' ";
		 
		 $q=mysql_query($query);
		
		 $output = array();
		 while($e = mysql_fetch_assoc($q))
				$output[]=$e;
				
		 return json_encode($output);		 
	 }	 
	 
 }