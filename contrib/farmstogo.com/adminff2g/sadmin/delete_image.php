<?
	require ('../../functions.php'); 
	if($id) {
		mysql_db_query($db,"DELETE FROM STP_IMAGES WHERE IMAGE_ID=$id");
	} else if($img) {
		mysql_db_query($db,"UPDATE STP_IMAGES SET image_name='$name' WHERE IMAGE_ID=$img");
	}	
	RetornarOL("./images_manager.php?subtype=$st");
?>	