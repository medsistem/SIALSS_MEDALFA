/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



$(document).ready(function () {
    $('#movimien').dataTable();

});

function cambiaLoteCadu(elemento) {
    var indice = elemento.selectedIndex;
    document.getElementById('SelectCadu').selectedIndex = indice;
}

function validaCantidad(e) {
    
//    alert('entro a validar');    
    var cantidadSol = document.getElementById('Cantidad').value;
    document.getElementById('Cant' + e).value = cantidadSol;
    var cantidadAlm = document.getElementById('CantAlm_' + e).value;
    if (parseInt(cantidadSol) > parseInt(cantidadAlm)) {
        alert('La cantidad a facturar no puede ser mayor a la cantidad de esa ubicación');
        return false;
    }


}
function validaCantidadMov(e) {
    var cantidadSol = document.getElementById('Cantidad').value;
    document.getElementById('Cant' + e).value = cantidadSol;
    var cantidadAlm = document.getElementById('CantAlm_' + e).value;


}