<? include ('../../functions.php'); ?>
<html>
<center>
<font face=verdana size=2>

<?
 
$rs = mysql_db_query ($database,"select id from testimonials");

for ($i=0; $i<=mysql_numrows($rs); $i++)
{
 $dt = mysql_fetch_array($rs);

 if ($borrar[$i]) { 
mysql_db_query ($database,"delete from testimonials where id=".$borrar[$i]); 
echo "FAQ <b>".$borrar[$i]."</b> Deleted <br>";
 }
}
?>
<script>
	window.location.href='./adminfaq.php';
</script>
</center>
</html>
