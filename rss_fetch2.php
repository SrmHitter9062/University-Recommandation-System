<?php header("refresh: 1740; url=http://localhost/NRS/rss_fetch2.php"); ?>
<html>
<body>

<?php
$news_rss = array();			//rss feed of news category
$news_rss[0] = 'http://economictimes.indiatimes.com/Markets/markets/rssfeeds/1977021501.cms';//State Govt. Colleges 
$news_rss[1] = 'http://indiatoday.feedsportal.com/c/33614/f/589706/index.rss?http://indiatoday.intoday.in/rss/article.jsp?sid=84';	//sports
//http://static.cricinfo.com/rss/livescores.xml    for cricket live scores
$news_rss[2]= 'http://timesofindia.indiatimes.com/rssfeeds/913168846.cms';  //Private Colleges
$news_rss[3] = 'http://feeds.hindustantimes.com/HT-HomePage-Entertainment';	//entertainment
$news_rss[4] = 'http://timesofindia.indiatimes.com/rssfeeds/3908999.cms';	//health
$news_rss[5]= 'http://indiatoday.feedsportal.com/c/33614/f/589702/index.rss?http://indiatoday.intoday.in/rss/article.jsp?sid=36';  //india
$news_rss[6] = 'http://rss.cnn.com/rss/edition_space.rss';	//Foreign Colleges
$news_rss[7] = 'http://timesofindia.indiatimes.com/rssfeeds/2886704.cms';	//lifestyle
$news_rss[8]= 'http://feeds.feedburner.com/ndtv/TqgX';  //IIITs
$news_rss[9]= 'http://timesofindia.indiatimes.com/rssfeeds/296589292.cms';  //world

$temp = key($news_rss);

$con = mysql_connect("localhost","root","");
if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }

mysql_select_db("news2", $con);

for ($i=0; $i<10; $i++){
$xml = simplexml_load_file($news_rss[$i]);

//echo $xml->getName() . "<br />";
$arr = array();
$arr[0] = NULL;
$arr[1] = NULL;
$arr[2] = NULL;
//$arr[3] = NULL;
$p = 0;
$x = NULL;
$type = 'State Govt. Colleges';
if($i == 0)
	$type = 'State Govt. Colleges';

if($i == 1)
	$type = 'sports';

if($i == 2)
	$type = 'Private Colleges';

if($i == 3)
	$type = 'entertainment';

if($i == 4)
	$type = 'health';

if($i == 5)
	$type = 'NITs';

if($i == 6)
	$type = 'Foreign Colleges';

if($i == 7)
	$type = 'lifestyle';

if($i == 8)
	$type = 'IIITs';

if($i == 9)
	$type = 'IITs';

	
	
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
					
					echo "titl1 : ". $x . "<br />";
					$x = addslashes($x);
					echo "title : ". $x. " <br />";
				}
				  if ($child3->getName() == "link")	{
					// $chksum = crc32($child3);
					//echo $chksum . "<br />";
					//$p = $child3;
					$arr[1] = $child3;
					$p = crc32($child3);
					
					echo $arr[1] . "<br />";
					echo "link : ".$p . "<br />";
					
					//echo $p. "<br />";
				}
					
				  if ( $child3->getName() == "pubDate") {
					$arr[2] = $child3;
					echo "pub : ". $arr[2] . "<br />";
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
				
				$result1 = mysql_query("select count(uid) from news_story where uid = '$p' and title ='$x' and link ='$arr[1]' and type_news = '$type' and date = '$arr[2]'");
				/*if (!mysql_query($result1,$con))
				{
					echo "hello";
					die('Error: ' . mysql_error());	
			
				}*/
				$row_1 = mysql_fetch_array($result1);
				//$row_1= mysql_fetch_array($sql_1);
				$count_1 = $row_1['count(uid)'];
				echo $count_1;
				if($count_1 == 0)
				{
				$sql="INSERT INTO news_story(uid,title,link,date,type_news,cnt) VALUES($p,'$x','$arr[1]','$arr[2]','$type',0)";
				if (!mysql_query($sql,$con))
				{
					echo "hello";
					die('Error: ' . mysql_error());	
			
				}
				
				}
				//$sq2="INSERT INTO story_visit(uid,title,link,count) VALUES($p,'$x','$arr[1]',0)";
				/*if (!mysql_query($result1,$con))
				{
					echo "hello";
					die('Error: ' . mysql_error());	
			
				}*/
				}
			}
							
				
			}
			
  }
  
			
  mysql_close($con);
?>

</body>
</html>
