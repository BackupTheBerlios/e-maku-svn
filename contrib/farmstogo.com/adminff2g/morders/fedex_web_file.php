<?  
require ('../../functions.php'); 

/*	
EIGTH BOX	13	104	10
QUARTER BOX	25	104	10
HALF BOX	25	104	18
FULL BOX	50	104	18
IN CMS
*/

	$inche = 2.5466;
	#HEIGHT, WIDTH, LENGTH, WEIGHT
	$HL = array(18, 25, 104, 17.2);
	$QT = array(10, 25, 104, 9.55);
	$EG = array(10, 25, 104, 9.55);
	$FL = array(10, 25, 104, 9.55);
			
	$f = "";
	if($f1) { $f = " AND I.ARRIVAL_DATE >= UNIX_TIMESTAMP('$f1 00:00:00') "; }
	if($f2) { $f .= " AND I.ARRIVAL_DATE <= UNIX_TIMESTAMP('$f2 23:59:59') "; }

	if (!$status) { $st = " AND F.ID_ORDER_STATUS='$status' ";}
	else if ($status=='ALL') { $st =""; }
	else if($status) { $st = " AND F.ID_ORDER_STATUS='$status'";}
	if($kw) {$skw = " AND (F.ID_FACTURA LIKE '%$kw%' OR F.SHIPPING_NAME LIKE '%$kw%' OR F.SHIPPING_LAST_NAME LIKE '%$kw%') ";}
			

			$querys = "SELECT F.*,I.*,P.*,S.code,C.CLIENT_EMAIL,Z.box_type FROM FACTURAS F, ITEMS_FACTURA I, PRODUCTOS P, STATES S, CLIENTS C, COUNTRIES X, SIZES Z WHERE ".
			"F.ID_FACTURA=I.ID_FACTURA AND F.ID_CLIENT=C.ID_CLIENT AND P.ID_PRODUCTO=I.ID_PRODUCTO AND S.ID_STATE=F.SHIPPING_ID_STATE AND X.ID_COUNTRY=F.SHIPPING_ID_COUNTRY ".
			"AND P.ID_SIZE=Z.ID_SIZE $f $st $skw ORDER BY I.ARRIVAL_DATE DESC limit 200";

			//echo $querys;
			$rs = mysql_db_query($database,$querys); 
			echo mysql_error();
			$template_fedex = Template_Fedex($database);	

			$file = "./files/WLABELS_".date("YmdHis").".in";
			$fp = fopen($file,"w");

			while($d = mysql_fetch_array($rs)) { 

				$line_fedex = $template_fedex;			
				//Para cada caja se debe generar la etiqueta FedEx
				$q =  (int)$d["ID_QUANTITY"];
			
				####Sacar el size del producto, la equivalencia Q,H,E,F
				if ($d["box_type"]=="Q") { list($h,$w,$l,$wg) = $QT; } 
				else if ($d["box_type"]=="H") { list($h,$w,$l,$wg) = $HL;}							
	
					#Aqui se ingresa el reemplazo de variables
					$line_fedex = str_replace("DESCRIPTION",str_replace('"','',substr($d["PRODUCT_DESC"],0,30)),$line_fedex);
					$line_fedex = str_replace("DESC2",str_replace('"','',substr($d["PRODUCT_DESC"],30,30)),$line_fedex);
					$line_fedex = str_replace("SHOP_NAME",str_replace('"','',substr($d["SHIPPING_NAME"]." ".$d["SHIPPING_LAST_NAME"],0,30)),$line_fedex);

					$address =  $d["SHIPPING_ADDRESS"];
					if (strlen($address) < 10) 
						{ $address =  $d["SHIPPING_ADDRESS"]." FDI"; }
					$line_fedex = str_replace("ADDRESS",str_replace('"','',$address),$line_fedex);

					$line_fedex = str_replace("XCITY",$d["SHIPPING_CITY"],$line_fedex);
					$line_fedex = str_replace("XPHONE",$d["SHIPPING_PHONE"],$line_fedex); 						
					$line_fedex = str_replace("XSTATE",$d["code"],$line_fedex);		
					$line_fedex = str_replace("PO_NO","FDWEB".$d["ID_FACTURA"],$line_fedex);				
					$line_fedex = str_replace("ITEM_NUMBER",$d["ID_PRODUCTO"]."/".$fmm["cod_farm"],$line_fedex);				
					$line_fedex = str_replace("QTY_ORD",$d["ID_QUANTITY"],$line_fedex);							
					$units = $d["BUNCHES_BOX"]*$d["STEMS_PER_BUNCH"];
					
					//echo $d["BUNCHES_BOX"]."-".$d["STEMS_PER_BUNCH"]."<br>";
					
					if (!$units) { $units = 1; }
					
					$line_fedex = str_replace("QTY_UNIDADES",$units*$d["ID_QUANTITY"],$line_fedex);	
					$line_fedex = str_replace("ZIP_CODE",$d["SHIPPING_ZIPCODE"],$line_fedex);	
					$country = "US";
					if(eregi('[A-Z]', $d["SHIPPING_ZIPCODE"])) {
						$country = "CA";
					} 
	
					$line_fedex = str_replace("XCOUNTRY",$country,$line_fedex);				
					$line_fedex = str_replace("PROCESS_DATE",$pdate,$line_fedex);
					$line_fedex = str_replace("CLIENT_MAIL",$d["CLIENT_EMAIL"],$line_fedex);
				
					$line_fedex = str_replace("UNIT_CUSTOM_VALUE",number_format(100*($d["ITEM_PRICE"]/$units),0,"",""),$line_fedex);
					$line_fedex = str_replace("CUSTOM_VALUE",number_format($d["CASE_PRICE"]*$d["ID_QUANTITY"],2,".",","),$line_fedex);
					$line_fedex = str_replace("TOTAL_WEIGHT",$wg*$d["ID_QUANTITY"],$line_fedex);
					$line_fedex = str_replace("HEIGHT",number_format(ceil($h/$inche),0,".",","),$line_fedex);
					$line_fedex = str_replace("WIDTH",number_format(ceil($w/$inche),0,".",","),$line_fedex);
					$line_fedex = str_replace("LENGTH",number_format(ceil($l/$inche),0,".",","),$line_fedex);
					$line_fedex = strtr($line_fedex, "áíóúñÑ", "aiounN");
					$line_fedex = str_replace("'","",$line_fedex);
					$line_fedex = str_replace("´","",$line_fedex);
					fwrite($fp,$line_fedex);
	
				}
	
	?>
	<script>
		window.location.href='<?echo $file?>';
	</script>	
	<?

#################################################
	function Template_Fedex($database) {
		$f = mysql_db_query($database,"SELECT code,value FROM m_web_fedex order by code");
		while($d = mysql_fetch_array($f)) {
			$data .= $d["code"].",\"".$d["value"]."\"\n";
		}
		$data .= "99,\"\"\n";
		return $data;
	}	

?>
</body>
</html>
