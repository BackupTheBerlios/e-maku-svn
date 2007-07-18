<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("ADMIN TIPO PRODUCTOS");
      
?>
<center>
<!-- CONTENIDO -->

<script>
	function Validar()
	{ 
	if (document.fm.nombre.value=="")
	 { alert ('All data are required'); return false; }
	}
</script>

<center>

<!-- CREAR -->

<table border="0" cellpadding=2 cellspacing=2>
  <tr bgcolor=ffffff> 
	<form name=fm action="admin_products2.php" onsubmit="return Validar()" method=post>
	
<!--
    <td valign=top><b>CODE</b><br> 
      <input type="text" name="cod" size=5 maxlength=5>
    </td> -->
        <td valign=top><b>Product Type</b><br> 
      <input type="text" name="nombre" size=25>
    </td>
	 <td valign=top><b>Order</b><br> 
      <input type="text" name="order" size=4>
    </td>
   
   <!--
    <td>State<br>
          <select name="st">
            <option value=""> </option>
            <? SelecTable($db,'m_states','cod_state','state_name',"WHERE cod_country='$ct'",'') ?> 
          </select>
    </td> -->
	   
     <th valign=bottom> 
      <input type="image" src="../images/ingresar.gif" border=0>
    </th>
  </tr>
  </form>
</table>

<hr noshade size=1 color=9966cc width=350>
<br>
<!--
<a href=""><b>Admin Domicilios / Oficinas</b><br>
-->


<!-- ADMIN -->

<br>
<table border="0" cellpadding=2 cellspacing=1>
  <tr class=htabla bgcolor="#6699cc"> 
  <form name=p action="admin_products2.php" method=post>
    		<input type=hidden name=ct value="<?echo $ct?>">		

    <th><font color=ffffff>PRODUCT</font></th>
	<th>Order</th>
    <th><font color=ffffff>Status</th>
   <th>&nbsp;</th>
  <th><font color=ffffff>Delete</font></th>
  </tr>
		<input type=hidden name=rid value="">
		<input type=hidden name=rvalor value="">
		<input type=hidden name=NORDER value="">

		
<?
 $st = mysql_db_query($db,"select * from TIPO_PRODUCTOS order by ID_TIPO_PRODUCTO");
 echo mysql_error();
 $i=1;
 while ($rs = mysql_fetch_array($st)) {	

	$sch = ""; $ach ="";
	if ($rs["status"]) { $sch = " checked=on"; }


	if (($i%2)==0) {$bg="cecece"; }
	else { $bg = "ffffff"; }
 ?>
  <tr bgcolor="<?echo $bg?>">
    <td><input type=text name="n<? echo $rs["ID_TIPO_PRODUCTO"]?>" value="<? echo $rs["TIPO_PRODUCTO"]?>" size=25></td>
	<td><input name="ORDER" type="text" id="ORDER" value="<? echo $rs["ORDER"]?>" size="4" dir="rtl" />  </td>
	<td align=center><input <?echo $sch?> type="checkbox" name="s<? echo $rs["ID_TIPO_PRODUCTO"]?>" value="1"></td>
    <td><input type=button name=mgrupo value="mod" onclick="document.p.rvalor.value=document.p.n<? echo $rs["ID_TIPO_PRODUCTO"]?>.value;document.p.rid.value='<? echo $rs["ID_TIPO_PRODUCTO"]?>';document.p.NORDER.value=document.p.ORDER.value;document.p.submit;document.p.submit();" ></td>
   <td align=center><input type="checkbox" name="d<? echo $rs["ID_TIPO_PRODUCTO"]?>" value="1"></td>
  </tr>
<? 
	$i++;
}?>  
</table>





<br>
      <input type="button" value="Process" onclick="this.form.submit;this.form.submit();">
 	  <input type=hidden name=st value="<?echo $st?>">		
   
  </form>
  
  
<?include('../end_admin.php'); ?>

  