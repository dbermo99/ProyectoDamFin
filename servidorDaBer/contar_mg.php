<?php
    include 'conexion.php';

    $consulta = "SELECT id, id_publicacion, count(*) FROM megusta GROUP BY id_publicacion";
    $resultado = $conexion ->query($consulta);

    while($fila = $resultado->fetch_array()){
        $megusta[] = array_map('utf8_encode',$fila);
    }

    echo json_encode($megusta);
    $resultado->close();
?>