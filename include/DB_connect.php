<?php
class DB_CONNECT{

    function __construct(){}   //constructor
    function __destruct(){}    //destructor

    public function connect(){
       // require_once 'config.php';     //including constant file
        // connecting to mysql
        $con = mysql_connect("localhost", "root", "") or die(mysql_error());
        // selecting database
        mysql_select_db("dailyplan_db") or die(mysql_error());

        // return database handler
        return $con;
    }   //handle the connection to database

    // Closing database connection
    public function close() { mysql_close(); }
}