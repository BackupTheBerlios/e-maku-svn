<?
include('../../functions.php');

    #CREACION DE REGISTRO detectando la variable nombre
if ($public_market != 1) {
   $public_market=0;
}
if (trim($nombre)) {
	$CQuery = "insert into MARKETS (MARKET,START_DATE,END_DATE,ACTIVATE_DATE,DESACTIVATE_DATE,public) values ('$nombre','$sd','$ed','$ad','$dd',$public_market)";
}

#MODIFICACION y ELIMINACION
else if (trim($rid)) {
	$CQuery =  "update MARKETS set MARKET='$rvalor',START_DATE='$sd',END_DATE='$ed',ACTIVATE_DATE='$ad',DESACTIVATE_DATE='$dd',public=$public_market where ID_MARKET='$rid'";
}
else {
	$st = mysql_db_query($db,"select p.ID_MARKET from MARKETS p left join PRODUCTOS s on p.ID_MARKET=s.ID_MARKET where s.ID_MARKET is NULL and p.ID_MARKET <> '' ");
	echo mysql_error();
	while ($rs = mysql_fetch_array($st)) {
		if (trim(${"d".$rs["ID_MARKET"]})) {
			mysql_db_query($db,"delete from MARKETS where ID_MARKET='".$rs["ID_MARKET"]."'");
		}
	}
}

if (trim($CQuery)) {
	mysql_db_query($db,$CQuery);
	if (mysql_error()) {
		$error=mysql_error();
		Alerta($error);
                     }
	//else { Alerta("Operación Realizada con Éxito"); }
}
	RetornarOL("admin_markets.php");
?>
