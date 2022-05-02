<?php
    include 'conexion.php';

    //DEVUELVE EL ID DE LA SIGUIENTE PUBLICACIÓN QUE SE CREE
    $consulta = "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'ProyectoFinal' 
        AND TABLE_NAME = 'publicacion'";
    $resultado = $conexion ->query($consulta);

    while($fila = $resultado->fetch_array()){
        $publicacion[] = array_map('utf8_encode',$fila);
    }

    echo json_encode($publicacion);
    $resultado->close();
?>