<? include ('../../functions.php'); 
?>
<html>
<head>
<title>Admin FAQ's</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<center>
<body bgcolor="#FFFFFF">
<font size=2 face=arial>
<table border="0" cellpadding=3 cellspacing=1 bgcolor=cecece>
  <tr>
<form action="pfaq.php" method=post name=pro> 
    <th><font face=verdana size=2>FAQ</th>
	<th></th>
    <th><font face=verdana size=2>Delete</th>
  </tr>
<?

$sel = "select * from testimonials where type='FQ' order by id DESC";

$rs = mysql_db_query($database,$sel);

if (mysql_num_rows($rs) > 0) {
for ($i=0; $i < mysql_num_rows($rs); $i++)
{
$dat = mysql_fetch_array($rs); 
?>
  <tr bgcolor=ffffff>
    <td><b><font face=verdana size=1><?echo $dat["fname"] ?></b>
    <br><font face=verdana size=1><?echo $dat["text"] ?></td>
		<td><a href="./nfaq.php?id=<?echo $dat["id"]?>"><font face=verdana size=1>Edit</a></td>
    <th><input type=checkbox name="borrar[]" value="<? echo $dat["id"] ?>"></th>
  </tr>
<?
}}
?>
</table><br>
<p>
<input type=submit value="Process">

</form>
  <p><a href="./nfaq.php">New FAQ</a>&nbsp; | &nbsp; <a href="reports.php">Go Back to Menu</a>


</body>
</html>