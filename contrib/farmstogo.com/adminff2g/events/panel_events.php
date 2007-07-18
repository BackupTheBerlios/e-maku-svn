<?php
session_start();
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("Events Report");
?>
<style>
BODY,input, select
{
    COLOR: #000000;
    FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT;
    FONT-SIZE: 11px
}
.table, .table th, .table td {border:1px solid #cecece; border-collapse:collapse; padding-left:2px; padding-right:2px;}
.table th { background:#ccccaa; }
.mixedyl { background:#FFFFCC}
</style>
<SCRIPT language=JavaScript src="../morders/date.js"></SCRIPT>
<?php
$replayed='NULL';
if(isset($_GET['replayed'])){
$replayed=1;
}
if(isset($_POST['replayed'])){
mysql_select_db($db,$shopping_db_link);
$date=date('Y-m-d');
foreach($_POST['replayed'] as $key => $value){
  if($value){
    mysql_query("update event_logs set replayed=1,reply_date=NOW(),reply='<html><head></head><body>Replayed by phone</body></html>' where id=$key",$shopping_db_link) or die(mysql_error());
	  }
	}

}
# CONECTARSE A LA BASE DE DATOS

# Ponemos ORDER BY ID_STANDING_ORDER_INFO si no hay uno definido
# CONSULTA PARA MOSTRAR LAS ORDENES FIJAS
$filter="";
if(isset($_POST['edate']) && $_POST['edate']!="" ){
$filter.=" and event_day >='".$_POST['edate']." 00:00:00'";
}
if(isset($_POST['rdate']) && $_POST['rdate']!="" ){
$filter .=" and reg_date >='".$_POST['rdate']." 00:00:00'";
}
if(isset($_POST['edate2']) && $_POST['edate2']!="" ){
$filter.=" and event_day <='".$_POST['edate2']."'";
}
if(isset($_POST['rdate2']) && $_POST['rdate2']!="" ){
$filter .=" and reg_date <='".$_POST['rdate2']."'";
}
$consulta ="SELECT id, name , email , dayt_phone , reg_date , budget , event_day, reply_date
	FROM event_logs WHERE replayed is NULL $filter order by reg_date DESC limit 200
";
if($replayed==1){
$consulta ="SELECT id, name , email , dayt_phone , reg_date , budget , event_day, reply_date
	FROM event_logs WHERE replayed=1 $filter order by reg_date DESC limit 200
";
}
# SE SACAN DATOS DE LA ORDEN FIJA
mysql_select_db($db,$shopping_db_link);
$rs =   mysql_query($consulta,$shopping_db_link) or die (mysql_error());
?>
<form id="form1" name="form1" method="post" action="<?php echo $PHP_SELF; ?>">
  <table border="0" align="center" cellpadding="0" cellspacing="0" class="table">
    <tr>
      <th colspan="12">Search</th>
    </tr>
    <tr>
      <td rowspan="2">Event Day</td>
      <td>From</td>
      <td><input name="edate" type="text" id="edate" value="<?php echo $_POST['edate']; ?>" size="10" /></td>
      <td><a href="javascript:show_calendar('form1.edate');"><img
    src="../images/calendario.gif"
    align=center border=0 /></a></td>
      <td rowspan="2">Reg Date</td>
      <td>From</td>
      <td><input name="rdate" type="text" id="rdate" value="<?php echo $_POST['rdate']; ?>" size="10" /></td>
      <td><a href="javascript:show_calendar('form1.rdate');"><img
    src="../images/calendario.gif"
    align=center border=0 /></a></td>
   </tr>
    <tr>
      <td>To</td>
      <td><input name="edate2" type="text" id="edate2" value="<?php echo $_POST['edate2']; ?>" size="10" /></td>
      <td><a href="javascript:show_calendar('form1.edate2');"><img
    src="../images/calendario.gif"
    align=center border=0 /></a></td>
      <td>To</td>
      <td><input name="rdate2" type="text" id="rdate2" value="<?php echo $_POST['rdate']; ?>" size="10" /></td>
      <td><a href="javascript:show_calendar('form1.rdate2');"><img
    src="../images/calendario.gif"
    align=center border=0 /></a></td>
    </tr>
    <tr>
      <td colspan="8"><div align="center">
        <input type="submit" name="Submit" value="Search" />
        <input type="reset" name="Submit2" value="Reset" />
      </div></td>
    </tr>
  </table>
</form>
        <p align="center"><a href="../reports.php">Go Back to Menu</a>
        <form id="form2" name="form2" method="post" action="<?php echo $PHP_SELF; ?>">
<table border=0 align="center" class="table">
<tr>
	<th>Name</th>
	<th>Email</th>
	<th>Day Phone</th>
	<th>Budget</th>
	<th>Msg</th>
	<th>Event Day</th>
	<th>Reg Date</th>
	<th>Reply</th>
	<th></th>
</tr>

<?
$class="";
while ($dt =   mysql_fetch_array ($rs))
 {
 	$class=($class=="")?"mixedyl":"";
?>
    <tr class="<?php echo $class; ?>">
	<td><?echo $dt["name"]?></td>
	<td><a href="mailto:<?echo $dt["email"]?>"><?echo $dt["email"]?></a></td>
	<td><?echo $dt["dayt_phone"]?></td>
	<td><?echo $dt["budget"]?></td>
	<td align=center><a href="javascript:OpenW('./d_event.php?id=<?echo  $dt["id"]?>','450','500')"><b>Info</b></a></td>
	<td><?echo $dt["event_day"]?></td>
 	<td><?echo substr($dt["reg_date"],0,10)?></td>
	<? if ($dt["reply_date"]) { ?>
	<td><?echo substr($dt["reply_date"],0,10)?> - <a href="javascript:OpenW('./vreply.php?id=<?echo  $dt["id"]?>','600','450',1)">View</a> </td>
	<?} else { ?>
		<td><b>	<a href="javascript:OpenW('./reply.php?id=<?echo  $dt["id"]?>','600','450',1)">Reply</a></td>
	<? } if($replayed!=1){ ?>
	<td align="center"><input <?php if (!(strcmp($dt['replayed'],1))) {echo "checked=\"checked\"";} ?> name="replayed[<?php echo $dt['id']; ?>]" type="checkbox" id="replayed[<?php echo $dt['id']; ?>]" value="1" /></td><?php } ?>
    </tr>
	<?
} if($replyed!=1){
?>
    <tr >
      <td colspan="10"><div align="right">
        <input type="submit" name="Submit3" value="Proccess&gt;&gt;" />
      </div></td>
    </tr>
		<?
}
?>

</table>
</form>
        <p><a href="../reports.php">Go Back to Menu</a>

</body>
</html>
