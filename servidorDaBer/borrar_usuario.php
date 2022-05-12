<?php
include 'conexion.php';

$id_usuario = $_POST['idUsuario'];
$fotoUsu = $_POST['fotoUsu'];

unlink("imagenes/$fotoUsu"); //BORRAR IMAGEN DEL SERVIDOR

$consulta = "DELETE FROM usuario WHERE id = $id_usuario";

mysqli_query($conexion,$consulta) or die (mysqli_error());
mysqli_close($conexion);

?>