<?php

// Inialize session
session_start();

// Check, if user is already login, then jump to secured page
if (!isset($_SESSION['username'])) {
        header('Location: http://localhost/NRS/securedpage.php');
}

?>
<?php
$user = $_SESSION['username'];
echo $user;
echo "<br />";
$c = $_GET['x'];
$tit = $_GET['t'];
$y = crc32($c);

echo $y;
echo "<br />";
echo $c;
echo "<br />";
$con1 = mysql_connect("localhost","root","");
if (!$con1)
  {
  die('Could not connect: ' . mysql_error());
  }
mysql_select_db("news2", $con1);
mysql_query("UPDATE news_story SET cnt = cnt+1 WHERE uid=$y ");
$result1 = mysql_query("select count(cnt) from user_interest where username ='$user' and title ='$tit'");
$row = mysql_fetch_array($result1);
$count1 = $row['count(cnt)'];

echo $count1; 
//echo $res;
if ($count1 == 0)
{
	mysql_query("insert into user_interest values('$user','$tit','$c','buisness',0)");
}
else
{
	mysql_query("update user_interest set cnt = cnt+1 where username = '$user' and title = '$tit' and link = '$c' ");
}
echo "<br />";


mysql_close($con1);
//header("Location: $c") ;

?>
