<?php session_start(); ?>
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
		
		<script type="text/javascript">
			function ExamReport(id)
			{
				var str1 = "exam_reports.php?id=";
				var str1=str1.concat(id,"&f=c");
				window.location = str1;
			}
	
		</script>
	
		<meta charset="UTF-8">
		<title>Municipapp</title>
		<link rel="stylesheet" href="css/style.css" type="text/css">
		
	</head>
	<body>
		<div id="background">
			<div id="page">
				<div id="header">
					<div id="logo">
						<img src="images/<?php echo htmlspecialchars($_SESSION['locality']);?>.png">
					</div>
					<div id="navigation">
						<ul>
							<li >
								<a href="home.php">Αρχικη</a>
							</li>
							<li>
								<a href="open_reports.php">Ανοικτες Αναφορες</a>
							</li>
							<li>
								<a href="in_progress_reports.php">Αναφορες σε εξελιξη</a>
							</li>
							<li class="selected">
								<a href="closed_reports.php">Κλειστες Αναφορες</a>
							</li>
						</ul>
					</div>
				</div>

				<div id="contents">
					<img src="images/header_img.jpg" alt="Img" style="margin-left:7px">
					<div class="box">
						<div>
							<div class="body">
								<h2>Κλειστες Αναφορες στο δημο <?php echo $_SESSION['locality']; ?></h2>
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
										$sql = "SELECT * FROM reports where state='closed' AND locality='".$_SESSION['locality']."';";
										$result = mysqli_query($con, $sql);
										echo "<h3>Βρέθηκαν  ".mysqli_num_rows($result)." αναφορές</h3><br/>";
										while($row = mysqli_fetch_assoc($result))
										{
											?><div margin='0 auto;'>
												<table border='.5'; bordercolor='green';>
													<tr><th height=15>ID</th><th height=15>Τοποθεσία</th><th height=15>Περιγραφή</th><th height=15>Εξέταση</th>
														<tr><td height=140; width=25; align='right'><?php echo $row['ID'] ?></td>
														<td height=170;  width=170; align='center'><img width='170' height='170' src='https://maps.googleapis.com/maps/api/staticmap?center=<?php echo $row['Latitude'] . "," . $row['Longitude'] ?>&markers=color:red%7C<?php echo $row['Latitude'] . "," . $row['Longitude'] ?>&zoom=15&size=200x200&key=AIzaSyAbde-wf7JVhyENoBZzSv7fMAqymTKE3xE'/></td>
														<td height=140;  width=400; align='justify'><font size='4' color='blue'><?php echo $row['date'] ?></font><br><font size='4'><b><?php echo $row['Type'] ?></b></font><br><br><font size='3'><?php echo $row['description'] ?></font></td>
														<td height=140;  width=80; align='center'><input type='image' width='80' height='120' src='images/exam_report.jpg' onclick='javascript:ExamReport(<?php echo $row['ID'] ?>);'/> </td>
													</tr>
												
												</table>
											</div>
											<?php
										}
									?>
								</p>
				
							</div>
						</div>
				
				
				
					</div>
				</div>
			<div id="footer">
				<p>
					© 2017 Πανεπιστημιο Αιγαιου. All Rights Reserved
				</p>
			</div>
		</div>
	</body>
</html>
