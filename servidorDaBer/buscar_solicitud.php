<?php
    include 'conexion.php';

    $posibleWhere = "";
    if(isset($_REQUEST['id_receptor'])) {
        $id_receptor = $_REQUEST['id_receptor'];
        $posibleWhere = "AND s.id_usuario2 = $id_receptor";
    }

    $consulta = "SELECT * FROM solicitud s, usuario u WHERE u.id = s.id_usuario1 $posibleWhere";

    $result = mysqli_query($conexion, $consulta);

    $solicitud = array();
    while($fila = mysqli_fetch_array($result)) {
        array_push($solicitud, $fila);
    }

    echo json_encode($solicitud, JSON_UNESCAPED_UNICODE);
    $result->close();
?>