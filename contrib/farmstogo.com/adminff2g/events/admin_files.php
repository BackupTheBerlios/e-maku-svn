<?
//require ('../../functions.php'); 
require ('../../s/include/functions.php'); 
//include('../head_admin.php');
Titulo_Maestro("Add Images");
$base = "../../stp_images/";


while (list ($key,$val) = @each ($_POST['del'])) {
		mysql_db_query($db,"insert into event_images (id_event,image) VALUES ('$id','$val')");
		//echo "insert into event_images (id_event,image) VALUES ('$id','$val')";
	?>
	<script>
		<!--
		//alert('Images added');
		this.opener.location.reload();
		window.close();			
		-->	
	</script>
	<?
}

?>
<style>
BODY
{
    COLOR: #000000;
    FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT;
    FONT-SIZE: 12px
}

P
{
    COLOR: #000000;
    FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT;
    FONT-SIZE: 12px
}
	
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
    FONT-SIZE: 12px;
    FONT-WEIGHT: Bold;
}


TR
{
    COLOR: #000000;
    FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT;
    FONT-SIZE: 12px
}

a
{
 FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT; 
 FONT-SIZE: 12px
}


input, select, textarea, button, form, submit 
{
  FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT; 
  FONT-SIZE: 12px
} 

</style>

<script>
<!--
	function OpenX(url,w,h,sb) {
		x = window.open(url,'XCF','menubar,toolbar=yes,scrollbars=yes,width='+w+',height='+h+',left=410,top=0'); //,toolbars=yes,resizable=yes, ,'OpenW','toolbars=yes,width='+w+',height='+h+',top=0,left=0');
		x.opener=self;
	}
-->	
</script>
<p>
<form name=one method=POST>	
<table cellpadding=5>
	<tr>
		<td align=center valign=top>
			<table border="0" cellpadding=3 cellspacing=1 bgcolor=cecece>
  		  		<tr bgcolor=ffffff> 
		    		<td colspan="2">
		        		<table width="100%" border="0" cellpadding=3 cellspacing=2>
		          			<tr>
		            			<td valign=top>
									<center><b> Lista de archivos en su directorio</b></center> <br>
				 					<!-- LISTA DE ARCHIVOS -->
									<?
									$d = dir($base);
									$i=0;
									while($entry=$d->read()) {
										if (strcmp($entry,"..")&&strcmp($entry,".")) {
											?>
				  							<nobr>
											<input type=checkbox name=del[] value='<?=$entry?>'>&nbsp;
											<?php
												$file_url = $base."/".$entry;
											?>
											<a href="javascript:OpenX('<?=$file_url?>','500','400','yes')"><?=$entry?></a>
											<br>
											<?php
											$i++;
			 							}
									}
									$d->close();
									?>
									<!-- FIN LISTA -->
								</td>
		          			</tr>
		        		</table>
		      		</td>
				</tr>
				<tr>
		   			<td align=center colspan=2 bgcolor=ffffff>
		         		<input type="hidden" name="i" value="<?=$i?>">
		         		<input type="hidden" name="id" value="<?=$id?>">
		         		<input type="submit" value="Add Images" <?= RB_STYLE?>>
		   			</td>
		  		</tr>
			</table>
		</td>
		<td valign=top>		  
		</td>
	</tr>
</table>
</form>


</body>
</html>
