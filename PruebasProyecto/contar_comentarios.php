<?php
    include 'conexion.php';

    $consulta = "SELECT id, id_publicacion, count(*) FROM comentario GROUP BY id_publicacion";
    $resultado = $conexion ->query($consulta);

    while($fila = $resultado->fetch_array()){
        $comentarios[] = array_map('utf8_encode',$fila);
    }

    echo json_encode($comentarios);
    $resultado->close();
?>