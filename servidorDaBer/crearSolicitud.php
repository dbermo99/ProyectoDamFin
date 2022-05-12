<?php
include 'conexion.php';

$id_usuario_solicitante = $_POST['id_usuario1'];
$id_usuario_receptor = $_POST['id_usuario2'];

if(isset($_REQUEST['eliminar'])) {
    $consulta = "DELETE FROM solicitud WHERE (id_usuario1 = '$id_usuario_solicitante' AND id_usuario2 = '$id_usuario_receptor') OR 
        (id_usuario2 = '$id_usuario_solicitante' AND id_usuario1 = '$id_usuario_receptor')";
} else {
    $consulta = "insert into solicitud (id_usuario1, id_usuario2) values('".$id_usuario_solicitante."','".$id_usuario_receptor."')";
}

mysqli_query($conexion,$consulta) or die (mysqli_error());
mysqli_close($conexion);

?>