<?php

$con = mysql_connect("localhost","root","");
if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }

mysql_select_db("news1", $con);
?>

<html>
<body>
<h1>This is a html page</h1>
<br />
<?php
$result = mysql_query("SELECT title , link FROM  `news_story` ORDER BY  `news_story`.`date` DESC ");
while($row = mysql_fetch_array($result))
  {
	$a = $row['title'];
	$b = $row['link'];
	echo "<br />";
	echo "<a href=http://localhost/NRS/domains/second.php?x=$b>$a</a>";
	echo "<br />";
 }


?>
</body>
</html> 
