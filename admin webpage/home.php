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
					<div class="dropdown">
					  <button class="dropbtn"><?php echo $_SESSION['full_name']; ?></button>
					  <div class="dropdown-content">
						<form method="post">
							<input type="submit" name="logout" id="logout" value="Αποσύνδεση" class="logoutbutton" style="vertical-align:middle"/>
						</form>
						<?php 
							function logout()
							{
								session_destroy();
								header("Location: index.php");
								die();
							}
							if(array_key_exists('logout',$_POST)){
							   logout();
							}
						?>

					  </div>
					</div>
					<div id="navigation">
						<ul>
							<li class="selected">
								<a href="home.php">Αρχικη</a>
							</li>
							<li>
								<a href="open_reports.php">Ανοικτες Αναφορες</a>
							</li>
							<li>
								<a href="in_progress_reports.php">Αναφορες σε εξελιξη</a>
							</li>
							<li>
								<a href="closed_reports.php">Κλειστες Αναφορες</a>
							</li>
						</ul>
					</div>
				</div>

				<div id="contents">
						<img src="images/header_img.jpg" alt="Img" style="margin-left:7px">
						<h1>ΔΗΜΟΣ <?php echo $_SESSION['locality']; ?></h1>
						<p>
							Η Δημοτική Αρχή πρέπει να παρακολουθεί τις αναφορές των χρηστών για ενδεχόμενα προβλημάτων προς επίλυση, καθώς και να
							φροντίζει την τήρηση των κανόνων λειτουργίας. Με τη συνεργασία μπορούν να επιτευχθούν τα πάντα!
						</p>
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
