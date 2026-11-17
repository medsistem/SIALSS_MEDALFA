/* SIALSS: tablas anchas (sin dependencias).
   Con el menú lateral, o en pantallas angostas, muchas tablas ya no caben en su panel y se salen.
   - Cada table.table que no esté ya en un contenedor con scroll se envuelve en .sialss-tabla-scroll
     (css/sialss-app.css): si no cabe, se desplaza a los lados en vez de salirse.
   - Las demás tablas (sin la clase "table") se envuelven solo si de verdad se salen de su contenedor:
     así no se tocan las tablas que solo acomodan un formulario.
   - Las tablas internas (dentro de otra tabla) no se tocan.
   - Las de DataTables no se envuelven: su .dataTables_wrapper ya tiene scroll por CSS.
   Lo cargan jspf/sialssBarra.jspf (pantallas con menú), jspf/piePagina.jspf (casi todas) y las
   pantallas que no incluyen ninguno de los dos. Si llega a cargarse dos veces, solo corre una. */
(function () {
  'use strict';
  if (window.sialssTablas) { return; }
  window.sialssTablas = true;

  var DENTRO_DE_SCROLL = '.sialss-tabla-scroll, .table-responsive, .dataTables_wrapper, .dataTables_scroll';

  function envolver(t) {
    var caja = document.createElement('div');
    caja.className = 'sialss-tabla-scroll';
    t.parentNode.insertBefore(caja, t);
    caja.appendChild(t);
  }

  function seSale(t) {
    var padre = t.parentElement;
    return !!padre && t.offsetWidth > 0 && t.offsetWidth > padre.clientWidth + 1;
  }

  function envolverTablas() {
    var tablas = document.querySelectorAll('table');
    Array.prototype.forEach.call(tablas, function (t) {
      if (!t.parentNode || !t.parentElement) { return; }
      if (t.parentElement.closest('table')) { return; }                       // tabla interna
      if (t.closest(DENTRO_DE_SCROLL) || t.closest('#sialss-menu, #sialss-barra')) { return; }
      if (t.classList.contains('table') || seSale(t)) { envolver(t); }
    });
  }

  // Filas de Bootstrap (.row, .form-horizontal .form-group) con margen de -15px dentro de algo sin
  // relleno (un panel, otra fila, un form): sobresalen y la página se mueve a los lados. Solo a las
  // que se salen de su contenedor se les quita el margen negativo.
  function ajustarFilas() {
    var filas = document.querySelectorAll('.row, .form-horizontal .form-group');
    Array.prototype.forEach.call(filas, function (f) {
      var padre = f.parentElement;
      if (!padre || f.offsetWidth === 0 || f.closest('#sialss-menu, #sialss-barra')) { return; }
      var rf = f.getBoundingClientRect(), rp = padre.getBoundingClientRect();
      if (rf.right > rp.right + 0.5 || rf.left < rp.left - 0.5) {
        f.style.marginLeft = '0';
        f.style.marginRight = '0';
      }
    });
  }

  function revisar() { ajustarFilas(); envolverTablas(); }

  function vigilar() {
    revisar();
    if (window.MutationObserver) {
      // Tablas que llegan después (AJAX): se espera un momento para que DataTables las arme primero.
      var pendiente = false;
      new MutationObserver(function (cambios) {
        if (pendiente) { return; }
        for (var i = 0; i < cambios.length; i++) {
          if (cambios[i].addedNodes.length) {
            pendiente = true;
            setTimeout(function () { pendiente = false; revisar(); }, 300);
            return;
          }
        }
      }).observe(document.body, { childList: true, subtree: true });
    }
    var espera = null;
    window.addEventListener('resize', function () {
      clearTimeout(espera);
      espera = setTimeout(revisar, 200);
    });
  }

  // Después de los $(document).ready de la página, para que DataTables ya haya armado sus tablas.
  function arrancar() { setTimeout(vigilar, 0); }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', arrancar);
  } else {
    arrancar();
  }
})();
