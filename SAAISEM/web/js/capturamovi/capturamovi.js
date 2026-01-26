/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*Captura movimiento*/

/* global Swal */

function justNumbers(e)
{
    var keynum = window.event ? window.event.keyCode : e.which;
    if ((keynum === 8) || (keynum === 46))
        return true;
    return /\d/.test(String.fromCharCode(keynum));
}

//function cambiaLoteCadu(elemento) {
//    var indice = elemento.selectedIndex;
//    document.getElementById('SelectCadu').selectedIndex = indice;
//}

function validaBuscar() {
    var Unidad = document.getElementById('ClaCli').value;
    if (Unidad === "") {
        alert('Seleccione Movimiento');
        return false;
    }

    var FechaEnt = document.getElementById('FechaEnt').value;
    if (FechaEnt === "") {
        alert('Seleccione Fecha de Entrega');
        return false;
    }
    var clave = document.getElementById('ClaPro').value;
    if (clave === "") {
        alert('Escriba una Clave');
        return false;
    }
}


function validaSeleccionar() {
//    alert('validar');
    var DesSel = document.getElementById('DesSel').value;
    if (DesSel === "") {
        alert('Favor de Capturar Toda la información');
        return false;
    }
    var ClaveSel = document.getElementById('ClaveSel').value;
    if (ClaveSel === "") {
        return false;
    }

    var cantidad = document.getElementById('Cantidad').value;
    if (cantidad === "") {
        alert('Escriba una cantidad');
        return false;
    }
    if (cantidad === "0") {
        alert('La cantidad debe ser mayor a cero');
        return false;
    }
//alert('validar: '+cantidad);
}




function validaRemision() {
//    alert('solo para validar remision');
    var confirmacion = confirm('Seguro que desea generar');
    var obs = $('#obs').val();
//    Swal.fire({
//        title: '¡Generar Movimiento!',
//        text: 'Seguro que desea generar.',
//        icon: 'info',
//        showDenyButton: true,
//        showCancelButton: true,
//        confirmButtonText: 'Confirmar',
//        denyButtonText: 'Cancelar',
//        // Posiciona el modal en el centro de la pantalla
//        inputAttributes: {
//            autocapitalize: 'off'
//        },
//        position: 'center',
//
////        timerProgressBar: true
//                // Puedes añadir más opciones para personalizar (colores, botones, etc.)
//    }).then((result) => {
//        if (result.isConfirmed) {
//            if (obs !== "") {
//                $('#btnGeneraFolio').prop('disabled', true);
//                return true;
//            } else {
//                alert("Favor de agregar Observación");
//                return false;
//            }
//        } else {
//            return false;
//        }
//
//    });

    if (confirmacion === true) {
        if (obs !== "") {
            $('#btnGeneraFolio').prop('disabled', true);
            return true;
        } else {
            alert("Favor de agregar Observación");
            return false;
        }
    } else {
        return false;
    }
}

$(document).ready(function () {
    $('#tablaMovimiento').dataTable();
});

function foco() {
    document.formulario1.ClaPro.focus();
    var ClaveSel = document.formulario1.ClaveSel.value;
    if (ClaveSel !== "") {
        document.formulario1.Cantidad.focus();
        window.scrollTo(0, 380);
    } else {
        document.formulario1.ClaPro.focus();
    }

}
/* Selecciona Lote*/
