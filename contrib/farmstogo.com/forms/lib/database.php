<?php
// +----------------------------------------------------------------------+
// | Flowerdealers.com - FD-Login Users	 								  |
// +----------------------------------------------------------------------+
// | PHP version 4.3.11                                                   |
// +----------------------------------------------------------------------+
// | Copyright (c) 2005 flowerdealers								      |
// +----------------------------------------------------------------------+
// | Authors: javier Andres Muñoz Hernandez <javier_munoz44@hotmail.com>  |
// +----------------------------------------------------------------------+


/**
* Biblioteca para funciones extendidas de BD usando PEAR 
* 
* @author javier Andres Muñoz Hernandez <javier_munoz44@hotmail.com>
* @version 1.0
* @access public
* @package TRLYDatabase
*/
	
	/**
    * selectDB.
    *
    * funcion que ejecuta un query de tipo Select 
    * devuelve las filas de la consulta en un arreglo de arreglos asociativos
	* 
    * @param  String   $stm					sentencia SQL a ejecutar
    * @param  object DB $db                 instancia de la conexión a la Base de Datos.    
    * @return array     $stack				arreglo de arreglos asociativos
    * @access public
    */
	function selectDB ($stm,$db){
		//echo $stm . "<br><br>";
		$stack = array ();
		$result = $db->query($stm);
		while ($row = $result->fetchRow()) {
		    array_push ($stack,$row);
		}
		return $stack;
	}

	function selectLimitDB ($stm, $from, $limit, $db){
//echo $stm;
		$result = $db->limitQuery($stm,$from, $limit);
		$stack = array ();
		while ($row = $result->fetchRow()) {
		    array_push ($stack,$row);
		}
		return $stack;
	}


?>