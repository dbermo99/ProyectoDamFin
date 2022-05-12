<?php
include 'conexion.php';

$id_usuario = $_POST['idUsuario'];
$id_publicacion = $_POST['idPublicacion'];

$consulta = "DELETE FROM megusta WHERE id_publicacion = $id_publicacion && id_usuario = $id_usuario";

mysqli_query($conexion,$consulta) or die (mysqli_error());
mysqli_close($conexion);

?>