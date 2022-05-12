<?php
include 'conexion.php';

$email = $_POST['mail'];
$usuario = $_POST['usuario'];
$password = $_POST['password'];

$consulta = "UPDATE usuario SET contrasenna = '".$password."' WHERE email = '".$email."' AND usuario = '".$usuario."'";
mysqli_query($conexion,$consulta) or die (mysqli_error());
mysqli_close($conexion);

?>