<?php

// Inialize session
session_start();

// Check, if user is already login, then jump to secured page
if (!isset($_SESSION['username'])) {
        header('Location: http://localhost/project1/securedpage.php');
}

?>
<?php
$name = $_SESSION['username'];
$con = mysql_connect("localhost","root","");
if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }

mysql_select_db("news2", $con);
?>

<html>
<head>
<style type="text/css">
#para1
{

color:black;
text-align:center;
} 
</style>
</head>

<body>
<div id="container" style="width:100%px">

<div id="header" style="background-color:#99CCFF;">
<h1 style="margin-bottom:0; font-size: xx-large;">News Recommender System</h1></div>
<h1>national News (courtsety: Times Of India )</h1>
<br />
<?php
$cur = date('d-m-Y H:i:s');
$cur = strtotime($cur);
mysql_query("UPDATE user_behaviour SET national= national+1
WHERE username= '$name'");
$result = mysql_query("SELECT title , link,date FROM  `news_story` WHERE type_news ='topstory' ORDER BY  `news_story`.`date` DESC ");
while($row = mysql_fetch_array($result))
  {
	//$link = urlencode($row['link']);
	$a = $row['title'];
	$b = $row['link'];
	$d = $row['date'];
	$timezone1 = "Asia/Calcutta";
	if(function_exists('date_default_timezone_set')) date_default_timezone_set($timezone1);
	//$d = date('d-m-Y H:i:s');
	$old_date_timestamp1 = strtotime($d);
	$diff = $cur-$old_date_timestamp1;
	if($diff<86400)
	{
		//echo $diff;
		echo "<a href='second.php?x=$b&t=$a'>$a</a>";;
	//echo $old_date_timestamp1;
	echo "<br />";
	echo "<br />";
	}
	//echo "<br />";
	//echo "<a href=second.php?x=$b&t=$a>$a</a>";
	//echo "<br />";
 }


?>

<div id="footer" style="background-color:#99CCFF;clear:both;text-align:center;">
  News recommender System (IIIT A)</div>

</div>
</body>
</html> 
