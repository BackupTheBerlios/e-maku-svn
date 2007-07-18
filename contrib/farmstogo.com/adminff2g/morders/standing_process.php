<? 
function Standing_orders_process($db) {
	$rs = mysql_db_query($db,"SELECT S. * , UNIX_TIMESTAMP( NEXT_ARRIVAL_DATE ) AS NAD, O.ID_CLIENT
FROM STANDING_ORDERS_INFO S
JOIN STANDING_ORDERS O ON ( O.STANDING_TICKET = S.STANDING_TICKET )
WHERE CURDATE( ) <= S.NEXT_ARRIVAL_DATE
AND S.ID_ORDER_STATUS =6
AND DATEDIFF( S.NEXT_ARRIVAL_DATE, DATE_ADD( CURDATE( ) , INTERVAL 8
DAY ) ) <=8");
	echo mysql_error();
	
	while($dt = mysql_fetch_array($rs)) {

		#INSERTANDO EL MAIN DE LA ORDEN
		$InsertQ = "insert into FACTURAS (DATE_TIME,ID_CLIENT,CREDIT_CARD_OWNER,CREDIT_CARD_NUMBER,CREDIT_CARD_EXPIRY,CREDIT_CARD_ADDRESS,
		ID_CREDIT_CARD_TYPE,SHIPPING_NAME,SHIPPING_LAST_NAME,SHIPPING_ADDRESS,SHIPPING_ZIPCODE,SHIPPING_PHONE,SHIPPING_FAX,
		SHIPPING_CITY,SHIPPING_ID_STATE,SHIPPING_ID_COUNTRY,SUBTOTAL_PRODUCTS,ID_SHIPPING_METHOD,SHIPPING_COST,SUBTOTAL_TAXES,
		GRAND_TOTAL,COMMENTS,COLOR_VARIETY_CHOICE,ID_ORDER_STATUS,ID_STANDING_ORDER_INFO,RETAILER_NUMBER,CVN,page)
		values (NOW(),".$dt["ID_CLIENT"].",'".$dt["CREDIT_CARD_OWNER"]."','".$dt["CREDIT_CARD_NUMBER"]."','".$dt["CREDIT_CARD_EXPIRY"]."','".$dt["CREDIT_CARD_ADDRESS"]."',
		'".$dt["ID_CREDIT_CARD_TYPE"]."','".$dt["CLIENT_NAME"]."','','".$dt["SHIPPING_ADDRESS"]."','".$dt["SHIPPING_ZIPCODE"]."','".$dt["SHIPPING_PHONE"]."','".$dt["SHIPPING_FAX"]."',
		'".$dt["SHIPPING_CITY"]."','".$dt["SHIPPING_ID_STATE"]."','".$dt["SHIPPING_ID_COUNTRY"]."','".$dt["SUBTOTAL_PRODUCTS"]."',1,'".$dt["SHIPPING_COST"]."',
		'".$dt["SUBTOTAL_TAXES"]."','".$dt["GRAND_TOTAL"]."','".$dt["COMMENTS"]."','',1,'".$dt["ID_STANDING_ORDER_INFO"]."',
		'".$dt["RETAILER_NUMBER"]."',".$dt["CVN"].",'ff2g')";

		mysql_db_query($db,$InsertQ) or die(mysql_error());
		$id=mysql_insert_id();
		
		mysql_db_query($db,"lock tables FACTURAS");
		$x = mysql_db_query($db,"select MAX(ID_FACTURA) from FACTURAS");
		$x = mysql_fetch_row($x);		
		mysql_db_query($db,"unlock tables");	

		$detail = "insert into ITEMS_FACTURA (ID_FACTURA,ID_CLIENT,ID_PRODUCTO,PRODUCT_DESC,
		ID_QUANTITY,ITEM_PRICE,ID_PROMOTION,ARRIVAL_DATE,DAY_CHARGE)
		SELECT $x[0],".$dt["ID_CLIENT"].",ID_PRODUCTO,PRODUCT_DESC,ID_QUANTITY,
		ITEM_PRICE,ID_PROMOTION,unix_timestamp('".$dt["NEXT_ARRIVAL_DATE"]."'),DAY_CHARGE
		from STANDING_ORDERS_PRODUCTS where 
		ID_STANDING_ORDER_INFO=".$dt["ID_STANDING_ORDER_INFO"];
		mysql_db_query($db,$detail) or die(mysql_error());
		
		if (!mysql_error()) {
			#Disminuir todas las variables necesarias
			$next = $dt["SHIP_SCHEDULE"]*86400*7+$dt["NAD"];
		
			$UpdSO = "UPDATE STANDING_ORDERS_INFO SET NEXT_ARRIVAL_DATE='".date("Y-m-d",$next)."',
			ORDERS_PENDING=(ORDERS_PENDING-1) WHERE STANDING_TICKET='".$dt["STANDING_TICKET"]."'"; 
					 
			mysql_db_query($db,$UpdSO) or die(mysql_error());
			
			mysql_db_query($db,"UPDATE STANDING_ORDERS_INFO SET ID_ORDER_STATUS=7 WHERE ORDERS_PENDING=0");
			echo mysql_error();
		}
	}
}
?>