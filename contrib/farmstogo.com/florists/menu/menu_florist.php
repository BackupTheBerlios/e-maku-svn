<?php 

$current_date = date("Y-m-d");

$current_date_timestamp = mktime (0, 0, 0, date("m"), date("d"), date("Y"));

$SO = 1;

//$db="ff2gv"; 
//$shopping_db_link=mysql_connect('localhost:8889', 'root', 'root');
mysql_select_db($db,$shopping_db_link) or die('Can not connect the database');
$query_tp="select distinct TIPO_PRODUCTOS.ID_TIPO_PRODUCTO,TIPO_PRODUCTOS.TIPO_PRODUCTO,TIPO_PRODUCTOS.ORDER

           from SUB_TIPO_PRODUCTOS,MARKETS,PRODUCTOS,TIPO_PRODUCTOS

          where SUB_TIPO_PRODUCTOS.STATUS=1

	    and TIPO_PRODUCTOS.ID_TIPO_PRODUCTO=SUB_TIPO_PRODUCTOS.ID_TIPO_PRODUCTO

            and PRODUCTOS.ID_MARKET=MARKETS.ID_MARKET

            and PRODUCTOS.ID_MARKET!=2

            and MARKETS.PUBLIC<>1

            and SUB_TIPO_PRODUCTOS.ID_SUB_TIPO_PRODUCTO=PRODUCTOS.ID_SUB_TIPO_PRODUCTO

            and TIPO_PRODUCTOS.ID_TIPO_PRODUCTO=PRODUCTOS.ID_TIPO_PRODUCTO

            and UNIX_TIMESTAMP(MARKETS.ACTIVATE_DATE) <= $current_date_timestamp

            and UNIX_TIMESTAMP(MARKETS.DESACTIVATE_DATE) >= $current_date_timestamp

			and TIPO_PRODUCTOS.STATUS=1

	    order BY TIPO_PRODUCTOS.ORDER, TIPO_PRODUCTOS.TIPO_PRODUCTO";
$tp=mysql_query($query_tp,$shopping_db_link) or die (mysql_error()); 
$row_tp=mysql_fetch_assoc($tp);
$tot_rows=mysql_num_rows($tp);
$foot=(($tot_rows+7)*17)-1;
?>
	<script language=JavaScript >
	
		var header = [['Products', null]];

	
	var menuHierarchy = [
	<?php do{ ?>
	["<?php echo $row_tp['TIPO_PRODUCTO']; ?>", null,
		<?php 
		$itp=$row_tp['ID_TIPO_PRODUCTO'];
		mysql_select_db($db,$shopping_db_link) or die('Can not connect the database');
	$query_stp="select distinct SUB_TIPO_PRODUCTOS.ID_SUB_TIPO_PRODUCTO,SUB_TIPO_PRODUCTO

           from SUB_TIPO_PRODUCTOS,MARKETS,PRODUCTOS

          where SUB_TIPO_PRODUCTOS.STATUS=1

            and PRODUCTOS.ID_MARKET=MARKETS.ID_MARKET

            and PRODUCTOS.ID_MARKET!=2

            and MARKETS.PUBLIC<>1

            and SUB_TIPO_PRODUCTOS.ID_SUB_TIPO_PRODUCTO=PRODUCTOS.ID_SUB_TIPO_PRODUCTO

            and UNIX_TIMESTAMP(MARKETS.ACTIVATE_DATE) <= $current_date_timestamp

            and UNIX_TIMESTAMP(MARKETS.DESACTIVATE_DATE) >= $current_date_timestamp

            and SUB_TIPO_PRODUCTOS.ID_TIPO_PRODUCTO=$itp";
				$stp=mysql_query($query_stp,$shopping_db_link) or die (mysql_error()); 
				$row_stp=mysql_fetch_assoc($stp);

		do{ ?>
		["<?php echo $row_stp['SUB_TIPO_PRODUCTO']; ?>","main_buy_list.php?spx=<?php echo $row_stp['ID_SUB_TIPO_PRODUCTO']; ?>"],
		<?php } while($row_stp=mysql_fetch_assoc($stp)); ?>
			],
	<?php } while($row_tp=mysql_fetch_assoc($tp)); ?>
		['&nbsp;', null],
		['Standing Orders',"../so/standing_orders.php"],
		['&nbsp;', null],
		['Links', "../s/home/links.php"]
		];
				var footer = [['&nbsp;', null]];

		</script>
