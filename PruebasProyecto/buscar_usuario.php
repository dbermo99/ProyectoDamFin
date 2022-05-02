<?php
    include 'conexion.php';

    if(isset($_REQUEST['usuario'])) {
        $usuario = $_REQUEST['usuario'];
        $posibleWhere = "WHERE usuario = '$usuario'";
    } else if(isset($_REQUEST['idUsuario'])) {
        $idUsuario = $_REQUEST['idUsuario'];
        $posibleWhere = "WHERE id_usuario2 = $idUsuario";
    } else
        $posibleWhere = "";
    
    $consulta = "SELECT * FROM  usuario $posibleWhere";

    $result = mysqli_query($conexion, $consulta);

    $usuario = array();
    while($fila = mysqli_fetch_array($result)) {
        array_push($usuario, $fila);
    }

    echo json_encode($usuario, JSON_UNESCAPED_UNICODE);
    $result->close();


?>