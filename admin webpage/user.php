<?php session_start();?>
<!DOCTYPE html>
<!-- Website template by freewebsitetemplates.com -->
<html>
	<head>
		<?php   
			if (!isset($_SESSION['username'])){
				header('Location:index.php');
				exit();
			}
		?>

		<meta charset="UTF-8">
		<title>Municipapp</title>
		<link rel="stylesheet" href="css/style.css" type="text/css">
		<script>
			function goBack() 
			{
				var url = window.location.href;
				window.location.href = 'exam_reports.php?id='.concat(url.substr(url.indexOf("rep=")+4,url.length));

			 }
		</script> 
	</head>
	
	<body>
	
		<div id="background">
			<div id="page">
				<div id="header">
					<div id="logo">
						<img src="images/<?php echo htmlspecialchars($_SESSION['locality']);?>.png">
					</div>
				</div>
				
				<div id="contents">
					<div class="body">
						<div class="box">
								<img onclick="javascript:goBack()" src="images/backbutton.png" style="width:120px;height:60px;border:0;"/>
							<br/><br/>								
							<h1>Στοιχεια Χρηστη</h1>
							<p>
								<?php
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

									$sql = "SELECT * FROM users, achievements where users.id=".$_GET['id']." AND achievements.user_id=users.id;";
									$sql2 = "SELECT * FROM reports where user_id=".$_GET['id'].";";
									$result = mysqli_query($con, $sql);
									$result2 = mysqli_query($con, $sql2);
									$row = mysqli_fetch_assoc($result);
									$fb_login=is_null($row['username']);
									?>
									
									<table style="width:100%">
										<tr><th width="120px">ID</th><td><?php echo $_GET['id'];?></td></tr>
										<tr><th width="120px">Όνομα Χρήστη</th><td><?php if(!$fb_login)echo $row['username']; else echo $row['fb_name'];?></td></tr>
										<tr><th width="120px">Φωτογραφία</th><td><?php if(!is_null($row['image']))echo'<img width="100" height="120" src="data:image/jpeg;base64,'.base64_encode( $row['image'] ).'"/><br/>';else echo'<i>Δεν υπάρχει<i>';?> </td></tr>
										<tr><th width="120px">E-mail</th><td> <?php if(!$fb_login)echo $row['email']; else echo $row['fb_email'];?></td></tr>
										<tr><th width="120px">Ημερομηνία Γένησης</th><td><?php if(!is_null($row['birthday']))echo $row['birthday']; else echo "<i>Δεν έχει οριστεί</i>"?></td></tr>
										<tr><th width="120px">Λογαριασμός</th><td><?php if(!$fb_login)echo "Είσοδος με λογαριασμό εφαρμογής"; else echo"Είσοδος με λογαριασμό Facebook";?></td></tr>
										<tr><th >Σύνολο Αναφορών</th><td><?php echo mysqli_num_rows($result2);?></td></tr>
										<tr><th width="120px">Σύνολο επιβεβαιώσεων</th><td><?php echo $row['total'];?></td></tr>
										<tr><th width="120px">Μέγιστες επιβεβαιώσεις</th><td><?php echo $row['single'];?></td></tr>
										<?php $sql = "SELECT * FROM reported_users where user_id=".$_GET['id'].";";
											$result = mysqli_query($con, $sql);
											?>
										<tr><th width="120px">Κατάσταση Χρήστη</th><td><?php if(mysqli_num_rows($result)>0) echo "Μπλοκαρισμένος"; else echo"Ενεργός";?></td></tr>
									</table>
									<br>
									
									<h2>Ενeργειες για το χρηστη με id: <?php echo $_GET['id'] ?></h2>
									<form method="post">
										<table class="state">
										<?php if(mysqli_num_rows($result)>0){echo'<tr><th width="80px"><input type="submit" name="Unblock" id="Unblock" value="Unblock" style="width:100px; height:29px " /></th>
										<td align="justify"><label>Άρση μπλοκαρίσματος Χρήστη</label></td></tr>';}
										else {echo'<tr><th width="80px"><input type="submit" name="Block" id="Block" value="Block" style="width:100px; height:29px "/></th>
										<td align="justify"><label>Μπλοκάρισμα χρήστη</label></td></tr>';}?>
										
										</table>
									</form>
									<br/><br/>
									<img  onclick="javascript:goBack()" src="images/backbutton.png" style="margin:0 auto;width:120px;height:60px;border:0;"/>
									<?php

									function Unblock()
									{
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
										$sql= "DELETE FROM reported_users WHERE user_id=".$_GET['id'].";";
										$result = mysqli_query($con, $sql);
										header("Refresh:0");
									}
									
									function Block()
									{
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
										$sql= "INSERT INTO reported_users (user_id) VALUES(".$_GET['id'].");";
										$result = mysqli_query($con, $sql);
										header("Refresh:0");
									}
									
					

									if(array_key_exists('Block',$_POST)){
									   Block();
									}
									if(array_key_exists('Unblock',$_POST)){
									   Unblock();
									}


									?>
									
									
							</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	
	
	</body>
</html>
