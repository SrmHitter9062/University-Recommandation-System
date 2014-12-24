
<?php
$c = $_GET['x'];
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
mysql_query("UPDATE story_visit SET count = count+1
WHERE uid=$y ");
mysql_close($con1);
header("Location: $c") ;

?>
