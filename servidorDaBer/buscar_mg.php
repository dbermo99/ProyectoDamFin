<?php
    include 'conexion.php';

    /*if(isset($_REQUEST['idPublicacion']) && isset($_REQUEST['idUsuario'])) {
        $idPublicacion = $_REQUEST['idPublicacion'];
        $idUsuario = $_REQUEST['idUsuario'];
        $posiblewhere = "WHERE id_publicacion = $idPublicacion OR id_usuario2 = $idUsuario";
    } else {
        $posiblewhere = "";
    }*/

    /*
    $consulta = "SELECT * FROM megusta";
    $resultado = $conexion ->query($consulta);

    while($fila = $resultado->fetch_array()){
        $megusta[] = array_map('utf8_encode',$fila);
    }

    echo json_encode($megusta);
    $resultado->close();
    */

    $consulta = "SELECT * FROM megusta";
    $result = mysqli_query($conexion, $consulta);

    $megusta = array();
    while($fila = mysqli_fetch_array($result)) {
        array_push($megusta, $fila);
    }

    echo json_encode($megusta, JSON_UNESCAPED_UNICODE);
    $result->close();
?>