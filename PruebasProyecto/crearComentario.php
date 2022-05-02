<?php

include 'conexion.php';

$id_publicacion = $_POST['id_publicacion'];
$id_usuario = $_POST['id_usuario'];
$comentario = $_POST['comentario'];

$consulta = "INSERT INTO comentario (id_publicacion, id_usuario, comentario) values('".$id_publicacion."','".$id_usuario."','".$comentario."')";
mysqli_query($conexion,$consulta) or die (mysqli_error());

mysqli_close($conexion);

?>