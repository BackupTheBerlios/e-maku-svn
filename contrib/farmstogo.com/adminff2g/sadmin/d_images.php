<? 	require ('../../functions.php'); ?>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<style>
	
TD
{
    COLOR: #000000;
    FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT;
    FONT-SIZE: 12px
}

TH
{
    COLOR: #000000;
    FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT;
    FONT-SIZE: 11px;
    FONT-WEIGHT: Bold;
}


TR
{
    COLOR: #000000;
    FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT;
    FONT-SIZE: 11px
}

a
{
 FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT; 
 FONT-SIZE: 12px
}

p
{
 FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT; 
 FONT-SIZE: 12px
}

input, select, textarea, button, form, submit 
{
  FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT; 
  FONT-SIZE: 11px
} 

</style>

<?
	$rs = mysql_db_query($db,"select P.TIPO_PRODUCTO,S.SUB_TIPO_PRODUCTO,S.DESC_SUB_TIPO_PRODUCTO  FROM TIPO_PRODUCTOS P, SUB_TIPO_PRODUCTOS S WHERE  P.ID_TIPO_PRODUCTO=S.ID_TIPO_PRODUCTO AND S.ID_SUB_TIPO_PRODUCTO=$st");
	echo mysql_error();
	$rs = mysql_fetch_array($rs);
	$title = $rs["TIPO_PRODUCTO"]." / ".$rs["SUB_TIPO_PRODUCTO"];
	$desc = $rs["DESC_SUB_TIPO_PRODUCTO"];
	define(IMAGES_DIR,"/stp_images/"); 
	
	
	$reg=4; # Numero de registros a desplegar
	if (!$p) {$p=1;}
	$l1 = ($p-1)*$reg;
	$limit = "limit $l1,$reg ";
?>

<title><?echo $title?></title>
<body bgcolor="#FFFFFF">
<FONT  size=2 face=arial>
<p><?echo "<b>$title</b><br>$desc"?><p>
<center>
<table bgcolor=cecece border="0" cellpadding=3 cellspacing=1>
  <tr bgcolor=ffffff>
  <?
	
	 $f = mysql_db_query($db,"select * from STP_IMAGES WHERE ST_ID=$st $limit");
	 $c = mysql_db_query($db,"select image_id from STP_IMAGES WHERE ST_ID=$st");
	 $dx = mysql_numrows($c);
	 while($ft = mysql_fetch_array($f)) {
			 if(($x%2)==0 && $x <> 0) { echo "</tr><tr bgcolor=ffffff>";}
  ?>
    <td align=center valign=middle><img hspace=5 vspace=5 src="<?echo IMAGES_DIR.$ft["image_file"]?>"></a><br><?echo $ft["image_name"]?></td>
   <? 
	$x++;  
  } ?>
  </tr>
</table>
<p>
<?
$pages = $dx/$reg;
if ($pages > 10) { $pages = 10; }

if (($pages) > 1) {
 echo "<br>Pages : ";

 for ($i=0; $i<$pages; $i++)
 { 
  if ($p<>($i+1)) { ?>
	<b><a href="d_images.php?st=<?echo$st?>&p=<?echo ($i+1)?>"><?echo ($i+1)?></a>&nbsp;</b>
<? }
 else { echo "<b>".($i+1)."</b>&nbsp;"; }
  }
 }
 ?>
 &nbsp;&nbsp;[ <a href="javascript:window.close()"><b>Close Window</b></a> ]
</center>
</body>
</html>
