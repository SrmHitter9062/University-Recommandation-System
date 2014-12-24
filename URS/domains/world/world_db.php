<html>
<body>

<?php

$con = mysql_connect("localhost","root","");
if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }

mysql_select_db("news2", $con);


$xml = simplexml_load_file("http://timesofindia.indiatimes.com/rssfeeds/296589292.cms");

//echo $xml->getName() . "<br />";
$arr = array();
$arr[0] = NULL;
$arr[1] = NULL;
$arr[2] = NULL;
//$arr[3] = NULL;
$p = 0;
$x = NULL;

foreach($xml->children() as $child)
  {
 // echo $child->getName() . ": " . $child . "<br />";
  
		  foreach($child->children() as $c)
		  {
	//		echo $c->getName() . ": " . $c . "<br />";
			
			foreach($c->children() as $child3)
		   {	
				 if ( $child3->getName() == "title") {
					$x = $child3;
					
					echo $x . "<br />";
					$x = addslashes($x);
				}
				  if ($child3->getName() == "link")	{
					// $chksum = crc32($child3);
					//echo $chksum . "<br />";
					//$p = $child3;
					$arr[1] = $child3;
					$p = crc32($child3);
					
					echo $arr[1] . "<br />";
					echo $p . "<br />";
					
					//echo $p. "<br />";
				}
					
				  if ( $child3->getName() == "pubDate") {
					$arr[2] = $child3;
					echo $arr[2] . "<br />";
				}

			}
			
			
			if($x != "" && $arr[1] != "" && $arr[2] != "" && $p != 0) {
				$arr[2]=str_replace( "GMT", "+0530", $arr[2] );
				echo $arr[2];
				echo "<br />";
				//$old_date=date('');
				$old_date_timestamp = strtotime($arr[2]);
				$new_date = date('Y-m-d H:i:s', $old_date_timestamp);  
				$arr[2]=$new_date;
				echo $arr[2];
				echo "<br />";
				
				
				$sql="INSERT INTO news_story1(uid,title,link,date,type_news,cnt) VALUES($p,'$x','$arr[1]','$arr[2]','IITs',0)";
				//$sq2="INSERT INTO story_visit(uid,title,link,count) VALUES($p,'$x','$arr[1]',0)";
				if (!mysql_query($sql,$con))
				{
					die('Error: ' . mysql_error());	
			
				}
				$sq2="INSERT INTO story_visit(uid,title,link,count) VALUES($p,'$x','$arr[1]',0)";
				
				//if (!mysql_query($sq2,$con))
				//{
					//die('Error: ' . mysql_error());	
			
				//}
			}
							
				
			}
			
  }
			
  mysql_close($con);
?>

</body>
</html>