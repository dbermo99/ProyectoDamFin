<?php
    include 'conexion.php';

    //DEVUELVE EL ID DEL SIGUIENTE USUARIO QUE SE CREE
    $consulta = "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'ProyectoFinal' 
        AND TABLE_NAME = 'usuario'";
    $resultado = $conexion ->query($consulta);

    while($fila = $resultado->fetch_array()){
        $usuario[] = array_map('utf8_encode',$fila);
    }

    echo json_encode($usuario);
    $resultado->close();
?>