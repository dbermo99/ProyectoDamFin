<?php
include 'conexion.php';

$id = $_POST['id'];
$email = $_POST['mail'];
$usuario = $_POST['usuario'];
$password = $_POST['password'];
$nombre = $_POST['nombre'];
$apellidos = $_POST['apellidos'];
$privada = $_POST['privada'];

$consulta = "UPDATE usuario SET email = '".$email."', usuario = '".$usuario."', contrasenna = '".$password."', 
    nombre = '".$nombre."', apellidos = '".$apellidos."', privada = '".$privada."'  WHERE id = '".$id."'";

mysqli_query($conexion,$consulta) or die (mysqli_error());
mysqli_close($conexion);

?>