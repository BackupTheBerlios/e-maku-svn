<? 
include('../../functions.php'); 
   
    #CREACION DE REGISTRO detectando la variable nombre
if (trim($nombre)) {
	$CQuery = "insert into TIPO_PRODUCTOS SET TIPO_PRODUCTO='$nombre', ORDER$order";
}

#MODIFICACION y ELIMINACION 
else if (trim($rid)) {
	$CQuery =  "UPDATE TIPO_PRODUCTOS SET `TIPO_PRODUCTO` = '$rvalor',
`ORDER` =$NORDER WHERE `ID_TIPO_PRODUCTO` = '$rid'";
}	
else {
	$st = mysql_db_query($db,"select p.ID_TIPO_PRODUCTO from TIPO_PRODUCTOS p left join PRODUCTOS s on p.ID_TIPO_PRODUCTO=s.ID_TIPO_PRODUCTO where s.ID_TIPO_PRODUCTO is NULL and p.ID_TIPO_PRODUCTO <> '' ");
	echo mysql_error();
	while ($rs = mysql_fetch_array($st)) {
		if (trim(${"d".$rs["ID_TIPO_PRODUCTO"]})) {
			mysql_db_query($db,"delete from TIPO_PRODUCTOS where ID_TIPO_PRODUCTO='".$rs["ID_TIPO_PRODUCTO"]."'");
		}
	}


	$st = mysql_db_query($db,"select p.ID_TIPO_PRODUCTO FROM TIPO_PRODUCTOS p");
	echo mysql_error();
	while ($rs = mysql_fetch_array($st)) {
	
		if (trim(${"s".$rs["ID_TIPO_PRODUCTO"]})) {
			mysql_db_query($db,"update TIPO_PRODUCTOS SET status=1 where ID_TIPO_PRODUCTO='".$rs["ID_TIPO_PRODUCTO"]."'");
		} else {
			mysql_db_query($db,"update TIPO_PRODUCTOS SET status=0 where ID_TIPO_PRODUCTO='".$rs["ID_TIPO_PRODUCTO"]."'");		
		}
	
	}


}

if (trim($CQuery)) {
	mysql_db_query($db,$CQuery);
	if (mysql_error()) { 
		$error=mysql_error(); 
		Alerta($error."  Query :".$CQuery); }
	//else { Alerta("Operación Realizada con Éxito"); }
}
	RetornarOL("admin_products.php");
?>