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
			function goBack() {
				var url_string = window.location.href;
				var f = url_string.substr(url_string.length - 1); 
				if(f=='o')
					window.location.href = 'open_reports.php';
				else if(f=='c')
					window.location.href = 'closed_reports.php';
				else
					window.location.href = 'in_progress_reports.php';
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
							
							<img onclick="javascript:goBack()"; src="images/backbutton.png"; style="width:120px;height:60px;border:0;"/>
							<br/><br/>
							<h1>Επισκοπιση Αναφορας</h1>
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

									$sql = "SELECT reports.ID,reports.user_id,reports.Category,reports.Type,reports.description,
									reports.Latitude,reports.Longitude,reports.Image,reports.Upvotes,reports.Downvotes,reports.Locality,
									reports.ImageComment,reports.date,reports.state,users.username,users.id 
									FROM reports,users where reports.ID=".$_GET['id']." AND reports.user_id=users.id;";
									$result = mysqli_query($con, $sql);
									$row = mysqli_fetch_assoc($result)
									?>
									
									<table style="width:100%">
										<tr><th width="120px">ID</th><td><?php echo $row['ID'];?></td></tr>
										<tr><th width="120px">Χρήστης</th><td><a href="user.php?id=<?php echo $row['id']."&rep=".$row['ID'];?>"><?php echo $row['username'];?></a></td></tr>
										<tr><th width="120px">Κατηγορία</th><td> <?php echo $row['Category'];?></td></tr>
										<tr><th width="120px">Τύπος</th><td> <?php echo $row['Type'];?></td></tr>
										<tr><th width="120px">Ημερομηνία</th><td><?php echo $row['date'];?></td></tr>
										<tr><th width="120px">Περιγραφή</th><td><?php echo $row['description'];?></td></tr>
										<tr><th width="120px"><img align="center" width="30" height="30" src="images/upvote_colored.png"></th><td><?php echo $row['Upvotes'];?></td></tr>
										<tr><th width="120px"><img align="center" width="30" height="30" src="images/downvote_colored.png"></th><td><?php echo $row['Downvotes'];?></td></tr>
										<tr><th width="120px">Φωτογραφία</th><td><?php if(is_null($row['Image']))echo"<i>Δεν υπάρχει</i>";  else echo'<img src="data:image/jpeg;base64,'.base64_encode( $row['Image'] ).'"/>';?></td></tr>
										<tr><th width="120px">Σχόλιο φωτογραφίας</th><td><?php if(is_null($row['ImageComment'])){echo"<i>Δεν υπάρχει</i>";} else echo"<i>".$row['ImageComment']."</i>";?></td></tr>
										<tr><th width="120px">Χάρτης</th><td><img width='300' height='170' src='https://maps.googleapis.com/maps/api/staticmap?center=<?php echo $row['Latitude'] . "," . $row['Longitude'] ?>&markers=color:red%7C<?php echo $row['Latitude'] . "," . $row['Longitude'] ?>&zoom=15&size=300x170&key=AIzaSyAbde-wf7JVhyENoBZzSv7fMAqymTKE3xE'/>
														<img width='300' height='170' src='https://maps.googleapis.com/maps/api/streetview?location=<?php echo $row['Latitude'] . "," . $row['Longitude'] ?>&size=300x170&key=AIzaSyAbde-wf7JVhyENoBZzSv7fMAqymTKE3xE'></td></tr>
										<tr><th width="120px" text="center">Κατάσταση</th><td><?php echo $row['state'];?></td></tr>
									</table>
									<br>
									<h2>Σχολια Αναφορας</h2>
									<div style="width:90%;margin:0 auto;overflow-y: auto; max-height:600px;background-color: #f2f2f2;">
										<table class="comments-table">
										<?php 
											$L0_sql= "SELECT comment,date FROM comments WHERE report_id=".$_GET['id']." AND parent_id=0;"; 
											$L1_sql= "SELECT id,user_id,comment,date FROM comments WHERE report_id=".$_GET['id']." AND parent_id IS NULL;"; 
											$user_info_query="SELECT username,facebook_id,image,fb_name FROM users WHERE id=";
											$L2_sql="SELECT id,user_id,comment,date FROM comments WHERE parent_id=";
											$result_L0 = mysqli_query($con, $L0_sql);
											$result_L1 = mysqli_query($con, $L1_sql);
											//$result_L2 = mysqli_query($con, $L2_sql);
											//$result_user_info = mysqli_query($con, $user_info_query);
											
											if (mysqli_num_rows($result_L0) > 0)
											{
												$row_L0 = mysqli_fetch_assoc($result_L0);
												echo'
												<tr>
													<td style="padding-left:10px;color:red;">Διαχειριστής<br/>
														<div class="dialogbox">
															<div class="comments">
																<span class="tip tip-up"></span>
																<div class="message">'
																	.$row_L0['comment'].'<br/>
																	<div style="text-align:right; color:blue">'.$row_L0['date'].'</div>
																</div>
															</div>
														</div>
													</td>
												</tr>
												';
											}
											while($row_L1 = mysqli_fetch_assoc($result_L1))
											{	
												$result_user_infoL1 = mysqli_query($con, $user_info_query.$row_L1['user_id']);
												$row_userL1= mysqli_fetch_assoc($result_user_infoL1);
												?>
												<tr>
													<td style="padding-left:110px"><?php if(!is_null($row_userL1['image']))echo'<img width="50" height="50" src="data:image/jpeg;base64,'.base64_encode( $row_userL1['image'] ).'"/><br/>';else echo'<img src="images/profile_image.png"/><br/>';?>  <a href="user.php?id=<?php echo $row_L1['user_id'] ?>&rep=<?php echo $_GET['id'] ?>"><?php if(is_null($row_userL1['username'])){ echo $row_userL1['fb_name'];} else {echo $row_userL1['username'];} ?> </a><br/>
														<div class="dialogbox">
															<div class="comments">
																<span class="tip tip-up"></span>
																<div class="message">
																	<span> <?php echo $row_L1['comment']; ?> </span><br/>
																	<div style="text-align:right; color:blue"><?php echo $row_L1['date']; ?></div>
																</div>
															</div>
														</div>
													</td>
												</tr>
												<?php
												$result_L2 = mysqli_query($con, $L2_sql.$row_L1['id']);
												while($row_L2 = mysqli_fetch_assoc($result_L2))
												{
													$result_user_infoL2 = mysqli_query($con, $user_info_query.$row_L2['user_id']);
													$row_userL2= mysqli_fetch_assoc($result_user_infoL2);
													?>
													<tr>
														<td style="padding-left:210px"><?php if(!is_null($row_userL2['image']))echo'<img width="50" height="50" src="data:image/jpeg;base64,'.base64_encode( $row_userL2['image'] ).'"/><br/>';else echo'<img src="images/profile_image.png"/><br/>';?>  <a href="user.php?id=<?php echo $row_L2['user_id'] ?>&rep=<?php echo $_GET['id'] ?>"><?php if(is_null($row_userL2['username'])){ echo $row_userL2['fb_name'];} else {echo $row_userL2['username'];} ?> </a><br/>
															<div class="dialogbox">
																<div class="comments">
																	<span class="tip tip-up"></span>
																	<div class="message">
																		<span> <?php echo $row_L2['comment']; ?> </span><br/>
																		<div style="text-align:right; color:blue"><?php echo $row_L2['date']; ?></div>
																	</div>
																</div>
															</div>
														</td>
													</tr>
													<?php
												}
												
												
											}
											?>
										
										
									</table>
									</div>
									<br/><br/>
									<h2>Αλλαγη Καταστασης Αναφορας</h2>
									<form method="post">
										<table class="state">
										<tr><th><input type="submit" name="open" id="open" value="open" style="width:100px; height:29px " /></th>
											<td><label>Αναφορές που δεν έχουν εξεταστεί</label></td></tr>
										<tr><th><input type="submit" name="examed" id="examed" value="In progress" style="width:100px; height:29px "/></th>
											<td><label>Αναφορές που έχουν εξεταστεί, εμπίπτουν στις αρμοδιότητες του δήμου</label></td></tr>
										<tr><th><input type="submit" name="closed" id="closed" value="closed" style="width:100px; height:29px"/></th>
											<td><label>Αναφορές που δεν έχουν εξεταστεί</label></td></tr>
										</table>
									</form> 
									<br/><br/>
									
									
									<img onclick="javascript:goBack()" src="images/backbutton.png" style="margin:0 auto;width:120px;height:60px;border:0;"/>
							

									<?php

									function open()
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
										$sql= "UPDATE reports SET state='Open' where ID=".$_GET['id'].";";
										$result = mysqli_query($con, $sql);
										header('Location: exam_reports.php?id='.$_GET['id']);
									}
									
									function examed()
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
										$sql= "UPDATE reports SET state='in progress' where ID=".$_GET['id'].";";
										$result = mysqli_query($con, $sql);
										header('Location: exam_reports.php?id='.$_GET['id']);
									}
									
									function closed()
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
										$sql= "UPDATE reports SET state='Closed' where ID=".$_GET['id'].";";
										$result = mysqli_query($con, $sql);
										header('Location: exam_reports.php?id='.$_GET['id']);
									}

									if(array_key_exists('open',$_POST)){
									   open();
									}
									if(array_key_exists('examed',$_POST)){
									   examed();
									}
									if(array_key_exists('closed',$_POST)){
									   closed();
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
