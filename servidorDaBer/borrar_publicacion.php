<?php
include 'conexion.php';

//$id_usuario = $_POST['idUsuario'];
$id_publicacion = $_POST['idPublicacion'];
$nombre_publicacion = $_POST['nombrePub'];

unlink("imagenes/$nombre_publicacion"); //BORRAR IMAGEN DEL SERVIDOR

$consulta = "DELETE FROM publicacion WHERE id = $id_publicacion";

mysqli_query($conexion,$consulta) or die (mysqli_error());
mysqli_close($conexion);

?>