<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("CUSTOMER LIST");


for ($i=0; $i<count($del); $i++) {
	mysql_db_query($db,"DELETE FROM CLIENTS WHERE ID_CLIENT=$del[$i]");
}

?>

<body bgcolor="#FFFFFF">

<center>
<p>
<? 
#######################################
?>

<table cellpadding=2 cellspacing=1>
  <tr><form>
    <td> Keyword<br>
        <input type="text" name="keyw">
    </td>
    <td><br>
        <input type="hidden" name="flag" value="1">
        <input type="submit" value="Search">
    </td>
    <td width=80></td>
    <td>
		<b><a href="javascript:print()">Print</a>
    </td>
  </tr>
</table>
</form>

<? 

#######################################
#Despliegue de resultados
?>
<form method=POST>
<table cellpadding=3 cellspacing=1 bgcolor=cecece>
  <tr>  
    <th>Client</th>
    <th>Address</th>
    <th>E-mail</th>
    <th>Phone</th>
    <th>Fax</th>
    <th>Delete</th>
  </tr>
<?

if ($keyw)  {$skeyw = " AND (C.CLIENT_NAME like '%$keyw%' OR C.CLIENT_LAST_NAME  like '%$keyw%' OR C.CLIENT_ADDRESS  like '%$keyw%' OR C.CLIENT_ZIPCODE  like '%$keyw%' OR ".
" C.CLIENT_EMAIL  like '%$keyw%' OR C.CLIENT_CITY  like '%$keyw%' OR S.STATE like '%keyw')"; }

$query = "SELECT DISTINCT C.ID_CLIENT,C.CLIENT_NAME,C.CLIENT_LAST_NAME,C.CLIENT_ADDRESS,C.CLIENT_ZIPCODE,".
"C.CLIENT_PHONE,C.CLIENT_FAX,C.CLIENT_EMAIL,C.CLIENT_CITY,S.code,P.COUNTRY_CODE FROM CLIENTS C, STATES S, ". 
" COUNTRIES P WHERE C.ID_STATE=S.ID_STATE AND C.ID_COUNTRY=P.ID_COUNTRY $skeyw ORDER BY C.CLIENT_LAST_NAME, C.CLIENT_NAME limit 200";   
//echo $query;

$rs = mysql_db_query($db,$query);
echo mysql_error();
while ($d = mysql_fetch_array($rs)){

?>
 <tr bgcolor=ffffff>
	<td><b><?echo $d["CLIENT_NAME"]." ".$d["CLIENT_LAST_NAME"]?></d></td>
    <td><?echo $d["CLIENT_ADDRESS"]."  | ".$d["CLIENT_CITY"].", ".$d["code"].", ".$d["CLIENT_ZIPCODE"].", ".$d["COUNTRY_CODE"]?></td>
    <td><a href="mailto:<?echo $d["CLIENT_EMAIL"]?>"><?echo $d["CLIENT_EMAIL"]?></a></td>
    <td><?echo $d["CLIENT_PHONE"]?></td>
    <td><?echo $d["CLIENT_FAX"]?></td>
    <td>
		<input type=checkbox name=del[] value="<?echo $d["ID_CLIENT"]?>">
    </td> 
 </tr>
<? } ?>
</table>
<P>
<! input type=submit value="Process">
</form>
<p><br>

</center>

<?include('../end_admin.php'); ?>
</body>
</html>
