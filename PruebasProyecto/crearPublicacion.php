<?php

include 'conexion.php';

$id_usuario = $_POST['id_usuario'];
$texto = $_POST['texto'];

if($_SERVER['REQUEST_METHOD']=='POST'){
 
    $imagen= $_POST['foto'];
    $nombre = $_POST['nombre'];

    // RUTA DONDE SE GUARDARAN LAS IMAGENES
    $path = "imagenes/$nombre.jpeg";

    //$actualpath = "http://localhost/PruebasProyecto/$path";

    //DECODIFICAMOS LA IMAGEN Y LA SUBIMOS AL SERVIDOR
    file_put_contents($path, base64_decode($imagen));

} else {
    $nombre = null;
}

//INSERTAMOS LOS DATOS EN LA BBDD
$consulta = "insert into publicacion (id_usuario, texto, foto) values('".$id_usuario."','".$texto."','".$nombre.".jpeg"."')";
mysqli_query($conexion,$consulta) or die (mysqli_error());

mysqli_close($conexion);

?>