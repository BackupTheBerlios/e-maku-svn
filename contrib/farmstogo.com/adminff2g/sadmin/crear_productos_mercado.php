<?
include('../../functions.php');

$SELECT_PRODUCTS = "select * from PRODUCTOS where id_market=4";
$resultado = mysql_db_query($db,$SELECT_PRODUCTS);
$i=2595;
while ($v = mysql_fetch_array($resultado)) {
$producto = nl2br($v[PRODUCTO]);
if ($v[DELIVERY_DELAY]==NULL) {
  $delivery = 'NULL';
} else {
  $delivery = $v[DELIVERY_DELAY];
}
if ($v[PRICE_STEM]==NULL) {
  $price_stem = 'NULL';
} else {
  $price_stem= $v[PRICE_STEM];
}
if ($v[STEMS_PER_BUNCH]==NULL) {
  $stems_bunch = 'NULL';
} else {
  $stems_bunch= $v[STEMS_PER_BUNCH];
}
if ($v[PRICE_BUNCH]==NULL) {
  $price_bunch = 'NULL';
} else {
  $price_bunch= $v[PRICE_BUNCH];
}
if ($v[BUNCHES_BOX]==NULL) {
  $bunches_box = 'NULL';
} else {
  $bunches_box= $v[BUNCHES_BOX];
}
if ($v[ID_PRODUCT_STATUS]==NULL) {
  $status = 'NULL';
} else {
  $status= $v[ID_PRODUCT_STATUS];
}

 $INSERT_PRODUCTS ="INSERT INTO PRODUCTOS VALUES ($i, $v[ID_SUB_TIPO_PRODUCTO], $v[ID_TIPO_PRODUCTO], $v[ID_SIZE], 19, '$producto',$price_stem , $stems_bunch, $price_bunch,$bunches_box, $delivery, $status, $v[COLOR_VARIETY_CHOICE],  '$v[QBCODE]', $v[minimal_box]);\n";
//$insertar = mysql_db_query($db,$INSERT_PRODUCTS);
echo $INSERT_PRODUCTS;
 $i++;
}

?>
