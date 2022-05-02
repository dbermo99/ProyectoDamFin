<?php
    include 'conexion.php';

    
    if(isset($_REQUEST['idUsuario'])) {
        $idUsuario = $_REQUEST['idUsuario'];
        $posibleselect = "COUNT(*)";
        $posibleFrom = "amistad";
        $posiblewhere = "WHERE id_usuario1 = $idUsuario OR id_usuario2 = $idUsuario";
    } else if(isset($_REQUEST['amistadesUsuario'])) { 
        //CON ESTO RECIBIMOS LA INFORMACION DE LOS USUARIOS CON LOS QUE UN USUARIO TIENE AMISTAD
        $idUsuario = $_REQUEST['amistadesUsuario'];
        $posibleselect = "a.id as 'idAmistad', a.id_usuario1, a.id_usuario2, 
            u.id as 'idUsuario', u.email, u.usuario, u.contrasenna, u.nombre, u.apellidos, u.privada, u.foto";
        $posibleFrom = "amistad a, usuario u";
        $posiblewhere = "WHERE (u.id LIKE a.id_usuario2 AND a.id_usuario1 LIKE $idUsuario) 
            OR (u.id LIKE a.id_usuario1 AND a.id_usuario2 LIKE $idUsuario)";
    } else {
        $posibleselect = "*";
        $posibleFrom = "amistad";
        $posiblewhere = "";
    }

    $consulta = "SELECT $posibleselect FROM $posibleFrom $posiblewhere";
    $result = mysqli_query($conexion, $consulta);

    $amistad = array();
    while($fila = mysqli_fetch_array($result)) {
        array_push($amistad, $fila);
    }

    echo json_encode($amistad, JSON_UNESCAPED_UNICODE);
    $result->close();

?>