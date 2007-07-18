<? 
   require('../../functions.php'); 
   
    $today = mktime (0,0,0,date("m"),date("d"),date("Y"));
	$f = "";
	if($f1) { $f = " AND I.ARRIVAL_DATE >= UNIX_TIMESTAMP('$f1 00:00:00') "; }
	if($f2) { $f .= " AND I.ARRIVAL_DATE <= UNIX_TIMESTAMP('$f2 23:59:59') "; }

	if (!$status) { $st = " AND F.ID_ORDER_STATUS IN (2,8)";}
	else if ($status=='ALL') { $st = " AND F.ID_ORDER_STATUS IN (2,8)"; }
	else if($status) { $st = " AND F.ID_ORDER_STATUS IN ($status)";}
	if($kw) {$skw = " AND (F.ID_FACTURA LIKE '%$kw%' OR F.SHIPPING_NAME LIKE '%$kw%' OR F.SHIPPING_LAST_NAME LIKE '%$kw%') ";}
			

	$querys = "SELECT F.*,I.*,S.code,C.CLIENT_EMAIL,Z.box_type FROM FACTURAS F, ITEMS_FACTURA I, STATES S, CLIENTS C, COUNTRIES X, ".
	" PRODUCTOS P LEFT OUTER JOIN SIZES Z ON Z.ID_SIZE=P.ID_SIZE WHERE F.ID_FACTURA=I.ID_FACTURA AND F.ID_CLIENT=C.ID_CLIENT AND ".
	" S.ID_STATE=F.SHIPPING_ID_STATE AND X.ID_COUNTRY=F.SHIPPING_ID_COUNTRY AND P.ID_PRODUCTO=I.ID_PRODUCTO AND ".
	" I.ARRIVAL_DATE > $today $f $st $skw ORDER BY I.ARRIVAL_DATE DESC limit 200";
   
	$file = "files/VM_".date("YmdHis").".CSV";
	$fp = fopen($file,"w");
		
	//echo $querys;
	$rs = mysql_db_query($database,$querys);
	echo mysql_error();
	if (mysql_numrows($rs)) {
		
	$head = strtoUpper("PO Number,Member Number,Member Name,Ship-to address,City,State,Postal Code,Item Number,Item Description,Quantity ordered,Order Date,Request date,Ship Via,Special notes/instructions,Unit Cost,Box\n");    
	fwrite($fp,$head);
	##################################
	#Arrancando el ciclo de despliegue
			
	while ($d = mysql_fetch_array($rs)){
		$line = $d["ID_FACTURA"].",".$d["ID_CLIENT"].",".str_replace(',','',$d["SHIPPING_NAME"]." ".$d["SHIPPING_LAST_NAME"]).",".str_replace(',','',trim(str_replace(chr(13),"",$d["SHIPPING_ADDRESS"]))).",".str_replace(',','',$d["SHIPPING_CITY"]).",".$d["code"].",".$d["SHIPPING_ZIPCODE"].",".$d["ID_PRODUCTO"].",".str_replace(',','',$d["PRODUCT_DESC"]).",".$d["ID_QUANTITY"].",".$d["DATE_TIME"].",".date("m/d/Y",$d["ARRIVAL_DATE"]).",FEDEX,,".$d["ITEM_PRICE"].",".$d["box_type"];
		$line = str_replace(chr(13),'',str_replace(chr(10),'',$line))."\n";
		fwrite($fp,str_replace('"','',$line));
	} 
			fclose($fp);
		?>
		<script>
			window.location.href='<?echo $file?>';
		</script>	
		<?
	}
?>
</body>
</html>