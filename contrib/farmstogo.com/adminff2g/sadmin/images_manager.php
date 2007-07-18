<?
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("Images Manager");
define(IMAGES_DIR,"/stp_images/");
define(FS_IMAGES_DIR,"../../stp_images/");
$db = $database;

#Guardando las imagenes
if($f) {

	for ($w=0; $w < count($f); $w++) {
	
		if(strcmp(trim($f_name[$w]),"")) {
			copy ($f[$w], FS_IMAGES_DIR.$f_name[$w]);
			//echo "insert into STP_IMAGES (st_id,image_file,image_name) VALUES ($subtype,'$f_name[$w]','$t[$w]')";
			mysql_db_query($db,"insert into STP_IMAGES (st_id,image_file,image_name) VALUES ($subtype,'$f_name[$w]','$t[$w]')");
		}
	}  

	echo mysql_error();

	$update = "UPDATE SUB_TIPO_PRODUCTOS SET DESC_SUB_TIPO_PRODUCTO='$desc' WHERE ID_SUB_TIPO_PRODUCTO=$subtype";
	mysql_db_query($db,$update);
	echo mysql_error();

	if (!mysql_error()) {
		//Alerta("Done !");
	} else {
		Alerta("The process had errors");
	}		
	RetornarOL("images_manager.php?subtype=$subtype");
} else {
?>
<center>

<script>
	function DelI(id) { 	
		if(confirm("Do you wish to delete this image?")) {
			window.location.href='./delete_image.php?id='+id+'&st=<?echo $subtype?>';	
		}	
	}	
	
		function Validar() {
			if (document.d.subtype.value=='') {
				alert('You must select a Sub Product');
			} else { document.d.submit(); }
		}
			
</script>

<?

$query = "SELECT S.SUB_TIPO_PRODUCTO,S.ID_SUB_TIPO_PRODUCTO,P.TIPO_PRODUCTO FROM ".
" SUB_TIPO_PRODUCTOS S, TIPO_PRODUCTOS P WHERE S.ID_TIPO_PRODUCTO=P.ID_TIPO_PRODUCTO ".
" AND DESC_SUB_TIPO_PRODUCTO NOT LIKE '%Only for standing orders%'".
" ORDER BY P.TIPO_PRODUCTO,S.SUB_TIPO_PRODUCTO ";
//echo $query;	

?>

  <p><form method=POST action="./images_manager.php" name=x>
    <select name="subtype" onchange="this.form.submit();">
		<option value="" selected>SELECT PRODUCT / SUB PRODUCT</option>
		<?
		$rs = mysql_db_query($db,$query);
		echo mysql_error();
		while($dt = mysql_fetch_array($rs)) { 
		if(trim($dt["SUB_TIPO_PRODUCTO"])) {
			$sel = "";
			if (!strcmp($dt["ID_SUB_TIPO_PRODUCTO"],$subtype)) { $sel = " selected"; } 
				?>
				<option value="<?echo $dt["ID_SUB_TIPO_PRODUCTO"]?>" <?echo $sel?>> <?echo strtoupper($dt["TIPO_PRODUCTO"]." / ".$dt["SUB_TIPO_PRODUCTO"])?> </option> 
				<? 
			}
		}
	?>
    </select> 
	
	<? if ($subtype) { ?>	
		&nbsp;&nbsp;<a target=other href="./d_images.php?st=<?echo $subtype?>"><b>View Page</b></a>
	<? } ?>


    </form>
  </p>

  <form name=d method=POST action="./images_manager.php" ENCTYPE="multipart/form-data">
  <table border="0"  cellpadding=3>
    <tr>
    <? if ($subtype) { ?>
      <td valign=top> 
      <? $rx = mysql_db_query($db,"SELECT * from STP_IMAGES WHERE st_id=$subtype ORDER BY image_id"); 
		if (mysql_numrows($rx)) {
      ?>
        <table  border="0" bgcolor=#cecece cellpadding=3 cellspacing=1>
          <tr> 
            <th colspan="3">Images in this SubProduct</th>
          </tr>
          <? 
          while($d = mysql_fetch_array($rx)) { ?>
          <tr bgcolor=#ffffff> 
		<td><?echo ++$i?></td>
            <td><input name=i<?echo $d["image_id"]?> type="text" value="<?echo $d["image_name"]?>"> <b><a href="<?echo IMAGES_DIR.$d["image_file"]?>" target=otra>(<?echo $d["image_file"]?>)<br></a></b></td>
            <td><a href="javascript:window.location.href='./delete_image.php?img=<?echo $d["image_id"]?>&st=<?echo $subtype?>&name='+document.d.i<?echo $d["image_id"]?>.value">Update</a></td>
		<td align=center><A href="javascript:DelI(<?echo $d["image_id"]?>)"><img src="../images/del.gif" border=0></a></td>
          </tr>
          <? } ?>
        </table>
       <? } ?> 
      </td>
     <? } ?> 
      <td width=25>
		&nbsp;
      </td>
      
      <td align=middle valign=top> 

        <table border="0" cellpadding=3 cellspacing=1 bgcolor=#cecece>
		<? if ($subtype) { 
			$sd = mysql_db_query($db,"SELECT DESC_SUB_TIPO_PRODUCTO FROM SUB_TIPO_PRODUCTOS WHERE ID_SUB_TIPO_PRODUCTO=$subtype");
			$sd = mysql_fetch_row($sd);
		?>
		 <tr><th colspan=2>Sub Product Description</th></tr>
		 <tr bgcolor=ffffdd><td colspan=2 align=center><textarea cols=80 rows=3 name=desc><?echo $sd[0]?></textarea></td></tr>	
		<? } ?>
		 <tr><th colspan=2>Insert New Images</th></tr>
          <tr bgcolor=#ffffff> 
            <td> Image 1<br> 
              <input type="file" name="f[]"><br>
              Image Name 1<br> 
              <input name="t[]" ><br>
            </td>
            <td> Image 2<br> 
              <input type="file" name="f[]"><br>
              Image Name 2<br> 
              <input name="t[]" ><br>
            </td>
          </tr>
          <tr bgcolor=#ffffff> 
            <td> Image 3<br>
              <input type="file" name="f[]"><br>
              Image Name 3<br> 
              <input name="t[]" ><br>
            </td>
            <td> Image 4<br>
              <input type="file" name="f[]"><br>
              Image Name 4<br> 
              <input name="t[]" ><br>
            </td>
          </tr> 

          <tr bgcolor=#ffffff> 
            <td> Image 5<br>
              <input type="file" name="f[]"><br>
              Image Name 5<br> 
              <input name="t[]" ><br>
            </td>
            <td> Image 6<br>
              <input type="file" name="f[]"><br>
              Image Name 6<br> 
              <input name="t[]" ><br>
            </td>
          </tr> 

          </table><p>
					  <input type=hidden name=subtype value="<?echo $subtype?>">	
                      <input type="button" value="Submit >>" onclick="Validar('<?echo $subtype?>')"></p>
      </td>
      </form>
    </tr>
  </table>
  <p><a href="reports.php">Go Back to Menu</a>
</div>
<? }  ?>
</body>
</html>

