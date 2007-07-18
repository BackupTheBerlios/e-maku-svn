<?php
// +----------------------------------------------------------------------+
// | 							FLOWERDEALERS							  |
// +----------------------------------------------------------------------+
// | PHP version 4.3.11                                                        |
// +----------------------------------------------------------------------+
// | Copyright (c) 2005 											      |
// +----------------------------------------------------------------------+
// | Authors:															  |	
// | Javier Andrés Muñoz Hernández <javier.munoz@corp.terralycos.com>  	  |
// +----------------------------------------------------------------------+
/**
 * En este archivo se hacen todas las consultas a la base de datos.
 * 
 * @author Javier Andrés Muñoz Hernández <javier.munoz@corp.terralycos.com> 
 * @version 1.0
 * @access public 
 * @package foros
 */

/**
 * Capa de persistencia.
 * 
 * 
 * @param integer $idparent id de la categoria padre
 * @param object $ DB $db      instancia de la conexión a la Base de Datos.
 * @return array $stack		arreglo de arreglos asociativos
 * @access public 
 */
// Se hace una funcion para cada una de las consultas que se van a realzar.



function validarUsuarios ($email, $password, $db){
	$stm = "select * from USERLOGIN WHERE EMAIL='$email' AND PASSWD='$password'";
	$result = selectDB($stm, &$db);
	return $result;
}

function validarUsuariosPoms ($email, $password, $db){
	$stm = "select * from s_users WHERE email='$email' AND passwd='$password'";
	$result = selectDB($stm, &$db);
	return $result;
}

function allUsers ($db){
	$stm = "select * from USERLOGIN ORDER BY FULL_NAME";
	$result = selectDB($stm, &$db);
	return $result;
}

function findUsers ($id, $db){
	$stm = "select * from USERLOGIN WHERE ID_USER='$id'";
	$result = selectDB($stm, &$db);
	return $result;
}

function updateUsuario ($email, $name, $company, $type, $id, $db){
	$stm= "UPDATE USERLOGIN SET EMAIL='$email', FULL_NAME='$name', COMPANY='$company', TYPE_USER=$type WHERE ID_USER=$id";
	//echo $stm;
	$result = $db->query($stm);
}

function insertUsuario ($email, $password, $name, $company, $type, $db){
	$stm= "INSERT INTO USERLOGIN (REG_DATE, EMAIL, PASSWD, FULL_NAME, COMPANY, TYPE_USER) VALUES (now(),'$email', '$password', '$name', '$company', $type)";
	//echo $stm;
	$result = $db->query($stm);
}

function deleteUsers ($id, $db){
	$stm = "DELETE FROM USERLOGIN WHERE ID_USER='$id'";
	$result = $db->query($stm);
}

function updatepassd ($password, $id, $db){
	$stm = "UPDATE USERLOGIN SET PASSWD='$password' WHERE ID_USER='$id'";
	//echo $stm;
	$result = $db->query($stm);
}
?>