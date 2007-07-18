<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("ADMIN SIZES");
      
?>
<center>
<!-- CONTENIDO -->

<script>
	function Validar()
	{ 
	if (document.fm.nombre.value==""||document.fm.cod.value=="")
	 { alert ('All data are required'); return false; }
	}
</script>

<center>

<!-- CREAR -->

<table border="0" cellpadding=2 cellspacing=2>
  <tr bgcolor=ffffff> 
	<form name=fm action="admin_sizes2.php" onsubmit="return Validar()" method=get>
	

        <td valign=top><b>Size</b><br> 
      <input type="text" name="cod" size=25>
    </td>

        <td valign=top><b>Size Long Desc</b><br> 
      <input type="text" name="nombre" size=45>
    </td>
	   
        <td valign=top><b>Box Type</b><br> 
      <input type="text" name="box_type" size="1">
    </td>
	   
     <th valign=bottom> 
      <input type="image" src="../images/ingresar.gif" border=0>
    </th>
  </tr>
  </form>
</table>

<hr noshade size=1 color=9966cc width=350>
<br>


<!-- ADMIN -->

<br>
<table border="0" cellpadding=2 cellspacing=1>
  <tr class=htabla bgcolor="#6699cc">   <form name=p action="admin_sizes2.php" method=post>
    		<input type=hidden name=ct value="<?echo $ct?>">		

    <th><font color=ffffff>CODE</font></th> 
    <th><font color=ffffff>SIZE</font></th>
    <th><font color=ffffff>SIZE LONG DESC</font></th>
    <th><font color=ffffff>BOX TYPE</font></th>
    <th><font color=ffffff>FEDEX COST</font></th>

    <th></th>
   <th><font color=ffffff>Delete</font></th>
  </tr>
		<input type=hidden name=rid value="">
		<input type=hidden name=rvalor value="">
		<input type=hidden name=rdescx value="">
		<input type=hidden name=box_type value="">
		<input type=hidden name=fedex_cost value="">

		
<?
 $st = mysql_db_query($db,"select * from SIZES order by SIZE");
 echo mysql_error();
 $i=1;
 while ($rs = mysql_fetch_array($st)) {	
	if (($i%2)==0) {$bg="cecece"; }
	else { $bg = "ffffff"; }
 ?>
  <tr bgcolor="<?echo $bg?>">
    <td  align=center><b><? echo $rs["ID_SIZE"]?></b></td> 
    <td><input type=text name="n<? echo $rs["ID_SIZE"]?>" value="<? echo $rs["SIZE"]?>" size=25></td>
    <td><textarea name="l<?echo $rs["ID_SIZE"]?>" cols=50 rows=2><? echo $rs["SIZE_LONG_DESC"]?></textarea></td>
    <td><input type=text name="box_type<? echo $rs["ID_SIZE"]?>" value="<? echo $rs["box_type"]?>" size="3"></td>
    <td align="center"><input type=text name="fedex_cost<? echo $rs["ID_SIZE"]?>" value="<? echo $rs["FEDEX_COST"]?>" size="4" dir="rtl"></td>
    <td><input type=button name=mgrupo value="mod" onclick="document.p.rdescx.value=document.p.l<? echo $rs["ID_SIZE"]?>.value;document.p.rvalor.value=document.p.n<? echo $rs["ID_SIZE"]?>.value;document.p.box_type.value=document.p.box_type<? echo $rs["ID_SIZE"]?>.value;document.p.rid.value='<? echo $rs["ID_SIZE"]?>';document.p.fedex_cost.value=document.p.fedex_cost<? echo $rs["ID_SIZE"]?>.value;document.p.rid.value='<? echo $rs["ID_SIZE"]?>';document.p.submit;document.p.submit();" ></td>
   <td align=center><input type="checkbox" name="d<? echo $rs["ID_SIZE"]?>" value="1"></td>
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

  