<?php
	session_start();
	//get POST data from form
	$username = $_POST['uname'];
	$password = $_POST['pass'];
	
	//connect to DB
	$servername = "localhost";
	$dbuser = "root";
	$dbpassword = "K!3wmpth";
	$dbname = "municipapp";
	$con = mysqli_connect($servername, $dbuser, $dbpassword, $dbname);
	// Check connection
	if (mysqli_connect_errno())
    {
	   echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }
	
	//Create QUERY	
	$sql = "SELECT username,password,locality,full_name FROM admins where username='$username' AND password='$password';";
	$result = mysqli_query($con, $sql);

	if (mysqli_num_rows($result) > 0)
	{	
		$row = mysqli_fetch_assoc($result);
		$_SESSION['username']=$row['username'];
		$_SESSION['locality']=$row['locality'];
		$_SESSION['full_name']=$row['full_name'];
		
		echo "<script type='text/javascript'>alert('IZI PIZI LEMON SQUEEZY')</script>";
		header('Location:home.php');
	}
	else
	{
		echo "<script type='text/javascript'>alert('Wrong username or password')</script>";
		header('Location:index.php');
	}
	
?>