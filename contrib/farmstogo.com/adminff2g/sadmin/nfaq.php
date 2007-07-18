<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("FAQ'S");

if (!$flag) {
?>

<html>
<head>
<title>FAQ's</title>
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
   alert ('Question + Answer are required');
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
      <td><font face=verdana size=2>Question<br>
        <input type="text" name="fname" size=58  value="<?echo $rs["fname"]?>">
      </td>     
      

      <td><font face=verdana size=2>Reg Date <br>
        <input type="text" name="xdate" size=10 value="<? if ($rs["date_reg"]) { 
					echo $rs["date_reg"]; } 
					else { echo date("Y-m-d"); }
					?>">
      </td>
</tr>
 

    <tr>
      <td colspan=2><font face=verdana size=2>Answer<br>
        <textarea name="texto" cols=80 rows=5 wrap=soft><?echo $rs["text"]?></textarea>
      </td>
    </tr>
    <tr>
      <th colspan=2> 
        <input type="button" name="Submit" value="Send >>" onclick="Validar()">
	<input type=hidden name=flag value="1">
	<input type=hidden name=xupd value="<?echo $upd?>">
	<input type=hidden name=xid value="<?echo $id?>">

</th>
    </tr>
  </table>
</form>
<? if ($i) { 
	echo "<b>FAQ Inserted";
}?>

<?
}
else
{

 
 if (!$xupd) {
 $insert = "insert into testimonials (date_reg,fname,email,text,type) values ('$xdate','$fname','$email','$texto','FQ')";
 $url = "nfaq.php?i=1";
 } else {
	$insert = "UPDATE testimonials SET date_reg='$xdate',fname='$fname',text='$texto' WHERE id=$xid";
	$url = "nfaq.php?id=$xid";
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
  <p><a href="./adminfaq.php">Admin FAQ's</a>&nbsp; | &nbsp; <a href="reports.php">Go Back to Menu</a>

<? include('../end_admin.php');

