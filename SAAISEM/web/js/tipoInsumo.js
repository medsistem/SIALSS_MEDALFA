$(document).ready(function () {
    $('#datosInsumoEspecial').dataTable();

});

document.getElementById("tipoIns").onchange = function () {
                        var tipoInsValue = this.value;
                        var insOncoSelect = document.getElementById("insOnco");

                        if (tipoInsValue === "Oncologico") {
                            insOncoSelect.style.display = "block";
                        } else {
                            insOncoSelect.style.display = "none";
                        }
                    };
                        

function validarForma(forma) {
        var ingresarClave = forma.ingresarClave;
        if (ingresarClave.value === "") {
            alert("Por favor, completar el campo de clave");
            return false;            
        }
        var tipoIns = forma.tipoIns;
        if(tipoIns.value === ""){
            alert("Por favor, completar el campo de tipo de insumo");
            return false;          
        }
        var insOnco = forma.insOnco;
        if(tipoIns.value === "Oncologico"){
            console.log("tipo de insumo " + tipoIns + "clave" + ingresarClave);
            if(insOnco.value === ""){
                console.log(insOnco + "insumo onco");
                alert("Por favor, completar el campo de tipo insumo oncologico");
                return false;
            }
        } 
        
    }
function descargarExcel(forma) {
    var insumoEspecial = forma.insumoEspecial;
    if (insumoEspecial.value === "" || insumoEspecial === 'null'){
        alert("Selecionar el tipo de insumo");
        return false;
    }
}
function filtrarClave() {
            var input, filter, select, option, i;
            input = document.getElementById('filtroClave');
            filter = input.value.toUpperCase();
            select = document.getElementById("ingresarClave");
            option = select.getElementsByTagName('option');

            for (i = 0; i < option.length; i++) {
                if (option[i].innerHTML.toUpperCase().indexOf(filter) > -1) {
                    option[i].style.display = "";
                } else {
                    option[i].style.display = "none";
                }
            }
        }

                       


