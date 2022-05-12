<?php
    include 'conexion.php';

    $posibleWhere = "";
    if(isset($_REQUEST['idUsuario'])) {
        $idUsuario = $_REQUEST['idUsuario'];
        $posibleWhere = "AND p.id_usuario = $idUsuario";
    } else if(isset($_REQUEST['misSeguidos'])) {
        $idUsuario = $_REQUEST['misSeguidos'];
        $posibleWhere = "AND (p.id_usuario IN (SELECT id_usuario1 FROM amistad WHERE id_usuario1 = $idUsuario OR id_usuario2 = $idUsuario) OR p.id_usuario 
            IN (SELECT id_usuario2 FROM amistad WHERE id_usuario1 = $idUsuario OR id_usuario2 = $idUsuario) OR p.id_usuario = $idUsuario)";
    }

    $consulta = "SELECT u.usuario, p.id, p.texto, p.foto, p.id_usuario FROM usuario u, publicacion p WHERE u.id = p.id_usuario $posibleWhere ORDER BY p.id DESC";
    $result = mysqli_query($conexion, $consulta);

    $pub = array();
    while($fila = mysqli_fetch_array($result)) {
        array_push($pub, $fila);
    }

    echo json_encode($pub, JSON_UNESCAPED_UNICODE);
    $result->close();


?>