<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("ADMIN PROMOTIONS");
     
if ($flag) {
	for ($i=0; $i<count($id); $i++) {
	
		if ($del[$i]) {
			mysql_db_query($db,"DELETE FROM PROMOTIONS WHERE ID_PROMOTION='".$del[$i]."'");		
		}
				
		if (trim(${"s".$id[$i]})) {
			mysql_db_query($db,"update PROMOTIONS SET ID_PROMOTION_STATUS=1 where ID_PROMOTION='".$id[$i]."'");
			echo mysql_error();
		} else {
			mysql_db_query($db,"update PROMOTIONS SET ID_PROMOTION_STATUS=2 where ID_PROMOTION='".$id[$i]."'");		
		}		
		
		echo mysql_error();
	}
}     
?>
<center>
<!-- CONTENIDO -->

<center>

<? 

$qry = "SELECT * FROM PROMOTIONS ORDER BY PROMOTION";
$rs = mysql_db_query($db,$qry);
echo mysql_error();

?>

<P>
<a href="promotions.php">New Promotion</a><P>
<p>


<form method=POST>
<table cellpadding=3 cellspacing=1 border="0" bgcolor=cecece>
  <tr> 
	<th>PROMOTION</th>
	<th>STATUS</th>
    <th>DELETE</td>
    
  </tr>
<? while($dt = mysql_fetch_array($rs)) { 

	$sch ="";
	if ($dt["ID_PROMOTION_STATUS"]==1) { $sch = " checked=on"; }
?>
  
  <tr bgcolor=ffffff> 
	<td><b><a href="promotions.php?id=<?echo $dt["ID_PROMOTION"]?>"><?echo $dt["PROMOTION"]?></a></b><br><?echo $dt["DESC_PROMOTION"]?></td>
    <td align=center><input <?echo $sch?> type="checkbox" name="s<? echo $dt["ID_PROMOTION"]?>" value="1"></td>
    <td align=center>
      <input type=checkbox name=del[] value="<?echo $dt["ID_PROMOTION"]?>">
    </td>    
          <input type=hidden name=id[] value="<?echo $dt["ID_PROMOTION"]?>">
  </tr>
<? } ?>  
</table>
<P>
<input type=hidden name=flag value="1">
<input type=submit value="Process Changes">
</form>



<?include('../end_admin.php'); ?>

  