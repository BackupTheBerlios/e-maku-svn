<?php include ('head_florist.php');
	$xPublic = "0";
//	if ($S_USER) { $xPublic="0"; }

?>
<script language="JavaScript">
<!--
function openNewWindow(URLtoOpen, windowName, windowFeatures) {
  newWindow=window.open(URLtoOpen,windowName,windowFeatures);}
-->
</script>
     <br>
	 <table border=0 align="center" cellpadding="0" cellspacing="0" width=100%>
        <tr>

		<td valign=top align=center>
  <?
	 $regs = 5;
	 $f = mysql_db_query($database,"select * from STP_IMAGES WHERE st_id=$spx limit $regs") or die(mysql_error());
	 $no_imgs = mysql_numrows($f);

	 echo mysql_error();
	 while($ft = mysql_fetch_array($f)) {
  ?>
    <a href="javascript:OpenW('../s/include/popup_image.php?img=<?php echo "../../stp_images/".$ft["image_file"]?>')">
	<img border=1 hspace=3 src="<?php echo "../../stp_images/".$ft["image_file"]; ?>" width=100></a>
	<font size=1><?echo $ft["image_name"]?></font><p>
   <?   } ?>

		</td>

          <td align=center valign=top>

<?
# CONSULTA PARA SACAR EL NOMBRE DEL SUB_TIPO DE PRODUCTO Y SU DESCRIPCION

$consulta_sub_tipo_producto = <<< EOQ
     select TIPO_PRODUCTO,SUB_TIPO_PRODUCTO,DESC_SUB_TIPO_PRODUCTO,ADDON
       from SUB_TIPO_PRODUCTOS,TIPO_PRODUCTOS
      where TIPO_PRODUCTOS.ID_TIPO_PRODUCTO=SUB_TIPO_PRODUCTOS.ID_TIPO_PRODUCTO
        and ID_SUB_TIPO_PRODUCTO=$spx
EOQ;

# CONSULTA PARA SACAR LOS PRODUCTOS SEGUN PARAMETRO SUB_TIPO_PRODUCTO

$consulta_size_productos = <<< EOQ
    select distinct SIZES.ID_SIZE,SIZES.SIZE,SIZES.SIZE_LONG_DESC,PRODUCTOS.PRODUCTO
       from SIZES,PRODUCTOS
      where PRODUCTOS.ID_PRODUCT_STATUS=1
        and SIZES.ID_SIZE=PRODUCTOS.ID_SIZE
        and PRODUCTOS.ID_MARKET!=2
        and PRODUCTOS.ID_SUB_TIPO_PRODUCTO=$spx
EOQ;

$consulta_addon_size_productos = <<< EOQ
    select distinct SIZES.SIZE,SIZES.ID_SIZE,SIZES.SIZE_LONG_DESC,PRODUCTOS.PRODUCTO,PRODUCTOS.ID_PRODUCTO
       from SIZES,PRODUCTOS
      where PRODUCTOS.ID_PRODUCT_STATUS=1
        and SIZES.ID_SIZE=PRODUCTOS.ID_SIZE
        and PRODUCTOS.ID_MARKET!=2
        and PRODUCTOS.ID_SUB_TIPO_PRODUCTO=$spx
EOQ;

# SACAMOS FECHA DE HOY PARA SABER SI EL MERCADO ESTA ACTIVO

$today  = mktime (0,0,0,date("m"),date("d"),date("Y"));

# CONSULTA DE MERCADOS POR TAMAÑO DE PRODUCTO

$consulta_market_productos = <<< EOM
    select distinct MARKETS.MARKET,MARKETS.ID_MARKET,PRODUCTOS.ID_PRODUCTO,PRODUCTOS.PRICE_STEM,
           PRODUCTOS.PRICE_BUNCH,PRODUCTOS.BUNCHES_BOX,
           STEMS_PER_BUNCH*BUNCHES_BOX as STEM_BOX, PRODUCTOS.minimal_box
      from PRODUCTOS,MARKETS
    where PRODUCTOS.ID_PRODUCT_STATUS=1
      and PRODUCTOS.ID_MARKET=MARKETS.ID_MARKET
      and PRODUCTOS.ID_SUB_TIPO_PRODUCTO=$spx
	  and UNIX_TIMESTAMP(MARKETS.ACTIVATE_DATE) <= $today
	  and UNIX_TIMESTAMP(MARKETS.DESACTIVATE_DATE) >= $today
      and PRODUCTOS.ID_MARKET!=2
      and MARKETS.public=$xPublic
      and PRODUCTOS.ID_SIZE=
EOM;

# CONSULTA DE MERCADOS POR TAMAÑO DE PRODUCTO ADD-ON

$consulta_addon_market_productos = <<< EOM
    select distinct MARKETS.MARKET,MARKETS.ID_MARKET,PRODUCTOS.ID_PRODUCTO,PRODUCTOS.PRICE_STEM,
           PRODUCTOS.PRICE_BUNCH,PRODUCTOS.BUNCHES_BOX,
           STEMS_PER_BUNCH*BUNCHES_BOX as STEM_BOX, PRODUCTOS.minimal_box
      from PRODUCTOS,MARKETS
    where PRODUCTOS.ID_PRODUCT_STATUS=1
      and PRODUCTOS.ID_MARKET=MARKETS.ID_MARKET
      and PRODUCTOS.ID_SUB_TIPO_PRODUCTO=$spx
      and MARKETS.public=$xPublic
	  and UNIX_TIMESTAMP(MARKETS.ACTIVATE_DATE) <= $today
	  and UNIX_TIMESTAMP(MARKETS.DESACTIVATE_DATE) >= $today
      and PRODUCTOS.ID_PRODUCTO=
EOM;


/*
$consulta_quantities_productos = <<< EOT
    select QUANTITIES.DESC_QUANTITY,QUANTITIES.QUANTITY,QUANTITIES.ID_QUANTITY from QUANTITIES
      left join QUANTITY_BUY using (ID_QUANTITY) where QUANTITY_BUY.ID_PRODUCTO=
EOT;
*/
# MOSTRAMOS EL SUB_TIPO DE PRODUCTO Y SU DESCRIPCION

$result_consulta_sub_tipo_producto = mysql_db_query($database,$consulta_sub_tipo_producto,$shopping_db_link);

$fila_sub_tipo_producto = mysql_fetch_array ($result_consulta_sub_tipo_producto);
$tipo_producto = $fila_sub_tipo_producto["TIPO_PRODUCTO"];
$sub_tipo_producto = $fila_sub_tipo_producto["SUB_TIPO_PRODUCTO"];
$desc_sub_tipo_producto = $fila_sub_tipo_producto["DESC_SUB_TIPO_PRODUCTO"];
$addon_tipo_producto = $fila_sub_tipo_producto["ADDON"];
?>
<script>
	function OpenW(page) {
	x = window.open(page,'X','toolbars=no,scrollbars=yes,resizable=yes,width=650,height=500');
	}

	function OpenX(page) {
		window.open(page,'CS','toolbars=no,resizing=no,width=500,height=350')
	}
</script>

<table width="90%" border=0>
<tr><td align="left"><font size="3" face="Verdana, Arial, Helvetica, sans-serif" color="#7342A5"><b><?php echo $tipo_producto." - ".$sub_tipo_producto; ?></b>
<? if ($no_imgs) { ?>
<a href="javascript:OpenW('../s/home/d_images.php?st=<?echo $spx?>')"><img hspace=10  src="../s/imgs/viewall.gif" border=0></a>
<? } ?>
</font></td>
</tr>
<tr><td align="justify"><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><?php echo $desc_sub_tipo_producto; ?></font></td></tr>
</table>
<br>
<?php

//if ($addon_tipo_producto == 0)       # GENERAMOS INFORMACION PARA TIPO DE PRODUCTO NORMAL
//{
    $result_size_productos = mysql_db_query($database,$consulta_size_productos.$id_size_producto." ORDER BY PRODUCTOS.PRODUCTO, SIZES.SIZE_LONG_DESC DESC",$shopping_db_link);
//}
//else if ($addon_tipo_producto == 1)  # GENERAMOS INFORMACION PARA PRODUCTOS TIPO ADD-ONS
//{
    //$result_size_productos = mysql_db_query($database,$consulta_addon_size_productos.$id_producto_producto." ORDER BY  PRODUCTOS.PRODUCTO, SIZES.SIZE_LONG_DESC DESC",$shopping_db_link);
    //echo mysql_error();
//}



# GENERAMOS LOS PRODUCTOS POR SIZE Y MARKET

 while ($fila_size_producto = mysql_fetch_array ($result_size_productos)) {

    $size_producto=$fila_size_producto["SIZE"];
    $id_size_producto=$fila_size_producto["ID_SIZE"];

    if ($addon_tipo_producto == 0)       	{
    $size_long_desc_producto=$fila_size_producto["SIZE_LONG_DESC"];
	}
    else {
    $size_long_desc_producto=$fila_size_producto["SIZE"];
	}

    $producto_producto=$fila_size_producto["PRODUCTO"];
    $id_producto_producto=$fila_size_producto["ID_PRODUCTO"];


//if ($addon_tipo_producto == 0)       # GENERAMOS INFORMACION PARA TIPO DE PRODUCTO NORMAL
//{
    $result_market_productos = mysql_db_query($database,$consulta_market_productos.$id_size_producto." AND PRODUCTOS.PRODUCTO='$producto_producto' ORDER BY MARKETS.ID_MARKET",$shopping_db_link);
//}
//else if ($addon_tipo_producto == 1)  # GENERAMOS INFORMACION PARA PRODUCTOS TIPO ADD-ONS
//{
    //$result_market_productos = mysql_db_query($database,$consulta_addon_market_productos.$id_producto_producto." ORDER BY MARKETS.ID_MARKET",$shopping_db_link);
	//echo "<!-- $consulta_addon_market_productos.$id_producto_producto -->";
//}

if (mysql_numrows($result_market_productos)) {

    echo "<center><table border=0 cellpadding=2 cellspacing=1 bgcolor=\"#7342A5\" width=100%><tr bgcolor=\"#Cecece\"><td align=\"left\" colspan=7>
    <font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=000000><b>$producto_producto $size_long_desc_producto</b></font></td></tr>\n";



    echo "<tr bgcolor=\"#ffffff\"><td align=\"center\"><font size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\"><a class=la href=\"#\" onclick=\"openNewWindow('help.html#market',name,'height=400,width=400,toolbar=no,scrollbars=yes')\">Market</a></font></td>\n<td align=\"center\"><font  size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\"><a class=la href=\"#\" onclick=\"openNewWindow('help.html#stemsbox',name,'height=400,width=400,toolbar=no,scrollbars=yes')\">Stems</a></font></td>\n".
	"<td align=\"center\"><font  size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\"><a class=la>\$/Stem</a></font></td>\n".
	"<td align=\"center\"><font  size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\"><a class=la href=\"#\" onclick=\"openNewWindow('help.html#pricebunch',name,'height=400,width=400,toolbar=no,scrollbars=yes')\">\$/Bunch</a></font></td>\n<td align=\"center\"><font  size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\"><a class=la href=\"#\" onclick=\"openNewWindow('help.html#bunchesbox',name,'height=400,width=400,toolbar=no,scrollbars=yes')\">Bunches</a></font></td>\n<td align=\"center\">".
	"<font  size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\"><a class=la href=\"#\" onclick=\"openNewWindow('help.html#pricebox',name,'height=400,width=400,toolbar=no,scrollbars=yes')\">\$/Box</a></font></td>\n<td align=\"center\"><font  size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\"><a class=la href=\"#\" onclick=\"openNewWindow('help.html#quantity',name,'height=400,width=400,toolbar=no,scrollbars=yes')\">Select Qty to Buy</a></font></td></tr>\n";

//echo $consulta_market_productos.$id_size_producto;
echo mysql_error();

    while($fila_market_producto = mysql_fetch_array ($result_market_productos)) {
	      $xq = $fila_market_producto["minimal_box"]; //1;
          $market_producto = $fila_market_producto["MARKET"];
          $id_producto_producto = $fila_market_producto["ID_PRODUCTO"];
          $price_stem_producto = $fila_market_producto["PRICE_STEM"];
          $stem_box_producto = $fila_market_producto["STEM_BOX"];
          $price_bunch_producto = $fila_market_producto["PRICE_BUNCH"];
          $bunches_box_producto = $fila_market_producto["BUNCHES_BOX"];

          $price_box_producto = $price_bunch_producto*$bunches_box_producto;
          $format_price_box_producto = sprintf ("%01.2f", $price_box_producto);
		  $stem_price =  sprintf ("%01.2f",$price_box_producto/$stem_box_producto);

         // echo "<form name=\"Buy_Product\" method=\"post\" action=\"\">";
          echo "<input type=\"hidden\" name=\"id_producto\" value=\"$id_producto_producto\">";
              echo "<tr bgcolor=\"#FFFFff\" align=\"center\" valign=\"middle\">\n<td nowrap align=\"center\"><font size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\"><b>".$market_producto."</b></font></td>\n<td nowrap align=\"center\"><font size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\">".$stem_box_producto."</font></td>\n".
			  "<td nowrap align=\"center\"><font size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\">".$stem_price."</font></td>\n
			   <td nowrap align=\"center\"><font size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\">$".$price_bunch_producto."</font></td>\n<td nowrap align=\"center\"><font size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\">".$bunches_box_producto."</font></td>\n<td nowrap align=\"center\"><font size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\"><b>$".$format_price_box_producto."</b></font></td>\n
			   <td nowrap align=\"center\"><font size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\"><div>&nbsp;
			   <select name=quantity_id_qty_producto>\n";
              //$result_quantities_productos = mysql_db_query($database,$consulta_quantities_productos.$id_producto_producto,$shopping_db_link);
			  //if (!mysql_numrows($result_quantities_productos)) {
			  //            $result_quantities_productos = mysql_db_query($database,$consulta_quantities_productos."1",$shopping_db_link);
			  //}

             while ($xq < 16) {
               //$quantity_desc_producto=$fila_quantities_producto["DESC_QUANTITY"];
               //$quantity_id_qty_producto=$fila_quantities_producto["ID_QUANTITY"];
               //$quantity_producto = $fila_quantities_producto["QUANTITY"];
               echo "<option value=".$xq.">".$xq." Box (".$xq*$bunches_box_producto.") Bunches</option>\n";
			$xq++;
              }
              echo "</select> <input type=\"image\" src=\"../s/imgs/ico_addcart.gif\" onclick=\"window.open('../s/home/registerform.php','register','location=0,status=0,menubar=0,scrollbars=1,width=700,height=500');\"></font></div></td></tr>";
              //mysql_free_result ($result_quantities_productos);
         }
     mysql_free_result ($result_market_productos);
     echo "</table><br>\n";
   }
   //mysql_free_result ($result_size_productos);
   //echo "</td></tr></table>";
	}
?>

</td></tr></table>

<center>
<? if ($no_imgs) { ?>
<a href="javascript:OpenW('../s/home/d_images.php?st=<?echo $spx?>')"><img hspace=10  src="../s/imgs/viewall.gif" border=0></a>
<? } ?>

	<?php require('end.php');?>



