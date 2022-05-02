<?php
include 'conexion.php';

$id = $_POST['id'];
$texto = $_POST['texto'];

$consulta = "UPDATE publicacion SET texto = '".$texto."' WHERE id = '".$id."'";

mysqli_query($conexion,$consulta) or die (mysqli_error());
mysqli_close($conexion);

?>