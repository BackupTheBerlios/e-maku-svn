<? 
include('../../functions.php'); 
   
    #CREACION DE REGISTRO detectando la variable nombre
if (trim($nombre)) {
	$CQuery = "insert into SIZES (SIZE,SIZE_LONG_DESC,box_type) values ('$cod','$nombre','$box_type')";
}

#MODIFICACION y ELIMINACION 
else if (trim($rid)) {
	$CQuery =  "update  SIZES set SIZE='$rvalor', SIZE_LONG_DESC='$rdescx', box_type='$box_type',FEDEX_COST=$fedex_cost where ID_SIZE='$rid'";
}	
else {
	$st = mysql_db_query($db,"select p.ID_SIZE from SIZES p left join PRODUCTOS s on p.ID_SIZE=s.ID_SIZE where s.ID_SIZE is NULL and p.ID_SIZE <> '' ");
	echo mysql_error();
	while ($rs = mysql_fetch_array($st)) {
		if (trim(${"d".$rs["ID_SIZE"]})) {
			mysql_db_query($db,"delete from SIZES where ID_SIZE='".$rs["ID_SIZE"]."'");
		}
	}
}

//echo $CQuery;
if (trim($CQuery)) {
	mysql_db_query($db,$CQuery);
	if (mysql_error()) { 
		$error=mysql_error()." Query : ".$CQuery; 
		Alerta($error); }
	//else { Alerta("Operación Realizada con Éxito"); }
}
	RetornarOL("admin_sizes.php");
?>