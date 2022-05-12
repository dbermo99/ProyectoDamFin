<?php
$hostname='localhost';
$database='ProyectoFinal';
$username='root';
$password='';

$conexion=new mysqli($hostname,$username,$password,$database);

if($conexion->connect_errno) {
    echo "Probelmas de conexion";
}
?>