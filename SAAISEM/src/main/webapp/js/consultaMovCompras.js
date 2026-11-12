$(document).ready(function () {
    $('#datosCompras').dataTable();
    $("#fecha").datepicker();
    $("#fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});
    //$('#btnRecalendarizar').attr('disabled', true);
    //$('#btnImpMult').attr('disabled', true);
});
$('#descargar').click(function () {

    var fecha_ini = $('#fecha_ini').val();
    var fecha_fin = $('#fecha_fin').val();
    var clave = $('#clave').val();

    var tresMesesDespues = new Date($('#fecha_ini').val());
    tresMesesDespues.setMonth(tresMesesDespues.getMonth() + 3);
   
   if((fecha_ini !== "") & (fecha_ini !== "") && (clave === "")){
       var fechaIni = new Date(fecha_ini);
       var fechaFin = new Date(fecha_fin);
       
    if (fechaFin.getTime() >= tresMesesDespues.getTime()) {
        swal("El intervalo de fechas no puede ser mayor a 3 meses desde la fecha de inicio.");
        return false;
    }};
    var clave = $('#clave').val();
    var origen = $('#Origen').val();
    var claCli = $('#ClaCli').val();
    if (claCli !== "") {
        console.log(claCli + clave + fecha_ini + fecha_fin + origen);
        if (clave === "" && fecha_ini === "" && fecha_fin === "" && origen === "0") {
            swal("Debes seleccionar clave, fecha u origen!");
            return false;
        }
        window.open("gnrMovCompras.jsp?clave=" + clave + "&fecha_ini=" + fecha_ini + "&fecha_fin=" + fecha_fin + "&origen=" + origen + "&claCli=" + claCli);
    } else
        swal("Debes seleccionar un concepto!");
    return false;
});

$('#btn_capturar').click(function () {

    var fecha_ini = $('#fecha_ini').val();
    var fecha_fin = $('#fecha_fin').val();
    var clave = $('#clave').val();

    var dosMesesDespues = new Date($('#fecha_ini').val());
    dosMesesDespues.setMonth(dosMesesDespues.getMonth() + 2);
   
   if((fecha_ini !== "") & (fecha_ini !== "") && (clave === "")){
       var fechaIni = new Date(fecha_ini);
       var fechaFin = new Date(fecha_fin);
    if (fechaFin.getTime() >= dosMesesDespues.getTime()) {
        swal("El intervalo de fechas no puede ser mayor a 2 meses desde la fecha de inicio.");
        return false;
    }};
    var clave = $('#clave').val();
    var origen = $('#Origen').val();
    var claCli = $('#ClaCli').val();
    if (claCli !== "") {
        console.log(claCli + clave + fecha_ini + fecha_fin + origen);
        if (clave === "" && fecha_ini === "" && fecha_fin === "" && origen === "0") {
            swal("Debes seleccionar clave, fecha u origen!");
            return false;
        }}
        
});




