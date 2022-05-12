<?php
include 'conexion.php';

$id_usuario = $_POST['idUsuario'];
$id_publicacion = $_POST['idPublicacion'];

$consulta = "INSERT INTO megusta(id_publicacion, id_usuario) VALUES('".$id_publicacion."','".$id_usuario."')";

mysqli_query($conexion,$consulta) or die (mysqli_error());
mysqli_close($conexion);

?>