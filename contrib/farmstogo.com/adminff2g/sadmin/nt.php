<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("TESTIMONIALS");

if (!$flag) {
?>

<html>
<head>
<title>Testimonials</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<style>

TD
{
    COLOR: #000000;
    FONT-FAMILY: Trebuchet Ms,Arial,Helvetica;
    FONT-SIZE: 11px
}

TH
{
    COLOR: #000000;
    FONT-FAMILY: Trebuchet Ms,Arial,Helvetica;
    FONT-SIZE: 11px;
    FONT-WEIGHT: Bold;
}


TR
{
    COLOR: #000000;
    FONT-FAMILY: Trebuchet Ms,Arial,Helvetica;
    FONT-SIZE: 11px
}
</style>





<body bgcolor="#FFFFFF">

<script>
function Validar()
{
 if(document.link.fname.value==''||document.link.texto.value=='')
   alert ('Full Name and Testimonail are required');
 else { document.link.submit(); document.link.submit;}
}
</script>

<?
$upd = 0;
if ($id) {
 $rs = mysql_db_query($db,"SELECT * from testimonials WHERE id=$id");
 $rs = mysql_fetch_array($rs);
 $upd = 1;
}

?>

<center><font size=2 face=arial>
<form method="post" name=link enctype="multipart/form-data">
  <table cellpadding=3 cellspacing=3  border="0">
    <tr>
      <td><font face=verdana size=2>Full Name<br>
        <input type="text" name="fname" size=40 value="<?echo $rs["fname"]?>">
      </td>     
      
           <td><font face=verdana size=2>E-mail <br>
        <input type="text" name="email" size=25 value="<?echo $rs["email"]?>">
      </td>

      <td><font face=verdana size=2>Reg Date <br>
        <input type="text" name="xdate" size=10 value="<? if ($rs["date_reg"]) { 
					echo $rs["date_reg"]; } 
					else { echo date("Y-m-d"); }
					?>">
      </td>
</tr>
 

    <tr>
      <td colspan=3><font face=verdana size=2>Testimonial<br>
        <textarea name="texto" cols=90 rows=5 wrap=soft><?echo $rs["text"]?></textarea>
      </td>
    </tr>
    <tr>
      <th colspan=3> 
        <input type="button" name="Submit" value="Send >>" onclick="Validar()">
	<input type=hidden name=flag value="1">
	<input type=hidden name=xupd value="<?echo $upd?>">
	<input type=hidden name=xid value="<?echo $id?>">
</th>
    </tr>
  </table>
</form>
<? if ($i) { 
	echo "<b>Testimonial Inserted";
}?>

<?
}
else
{
if (!$xupd) {
 $insert = "insert into testimonials (date_reg,fname,email,text,type) values ('$xdate','$fname','$email','$texto','TS')";
 $url = "nt.php?i=1";
 } else {
	$insert = "UPDATE testimonials SET date_reg='$xdate',fname='$fname',email='$email',text='$texto' WHERE id=$xid";
	$url = "nt.php?id=$xid";
 }
 
 mysql_db_query($database,$insert);
 echo mysql_error();
 ?>
 <SCRIPT>
	window.location.href='<?echo $url;?>';
 </script>
 <?
}
?>
  <p><a href="./admint.php">Admin Testimonials</a>&nbsp; | &nbsp; <a href="/adminff2g/reports.php">Go Back to Menu</a>

<? include('../end_admin.php');

