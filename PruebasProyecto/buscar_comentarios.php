<?php
    include 'conexion.php';

    $posibleWhere = "";
    if(isset($_REQUEST['id_publicacion'])) {
        $id_publicacion = $_REQUEST['id_publicacion'];
        $posibleWhere = "AND c.id_publicacion = $id_publicacion";
    }

    $consulta = "SELECT c.id, u.usuario, c.comentario FROM comentario c, publicacion p, usuario u WHERE c.id_publicacion = p.id 
        AND c.id_usuario = u.id $posibleWhere";
    $result = mysqli_query($conexion, $consulta);

    $comentario = array();
    while($fila = mysqli_fetch_array($result)) {
        array_push($comentario, $fila);
    }

    echo json_encode($comentario, JSON_UNESCAPED_UNICODE);
    $result->close();
?>