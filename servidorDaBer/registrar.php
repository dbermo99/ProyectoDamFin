<?php
include 'conexion.php';

$email = $_POST['mail'];
$usuario = $_POST['usuario'];
$password = $_POST['password'];
$nombre = $_POST['nombre'];
$apellidos = $_POST['apellidos'];
$privada = $_POST['privada'];

if($_SERVER['REQUEST_METHOD']=='POST'){
 
    $imagen= $_POST['foto'];
    $nombreFoto = $_POST['nombreFoto'];

    // RUTA DONDE SE GUARDARAN LAS IMAGENES
    $path = "imagenes/$nombreFoto.jpeg";

    //$actualpath = "http://localhost/PruebasProyecto/$path";

    file_put_contents($path, base64_decode($imagen));

    echo "SE SUBIO EXITOSAMENTE";

} else {
    $nombreFoto = null;
}

$consulta = "insert into usuario (email, usuario, contrasenna, nombre, apellidos, privada, foto) 
    values('".$email."','".$usuario."','".$password."','".$nombre."','".$apellidos."','".$privada."','".$nombreFoto.".jpeg"."')";
mysqli_query($conexion,$consulta) or die (mysqli_error());
mysqli_close($conexion);

?>