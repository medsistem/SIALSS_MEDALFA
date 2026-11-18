/* Vista previa de las cargas por Excel: 50 renglones por página, con la tabla dentro de un marco con desplazamiento
   (.sialss-mr__desplaza) y botones Primera · Anterior · Página N de M · Siguiente · Última debajo.
   Uso: SialssTablaPaginada.mostrar(tbody, filas, columnas, aCeldas, maximo)
     filas: arreglo de renglones; aCeldas(fila) devuelve el arreglo de textos de cada celda;
     maximo: tope de renglones que manda el servidor (avisa si se llegó a él). Sin dependencias. */
(function () {
  'use strict';
  var POR_PAGINA = 50;
  var miles = function (n) { return Number(n).toLocaleString('es-MX'); };

  function celda(t) { var td = document.createElement('td'); td.textContent = t == null ? '' : t; return td; }

  function boton(texto, etiqueta) {
    var b = document.createElement('button');
    b.type = 'button';
    b.className = 'sialss-boton sialss-paginas__boton';
    b.textContent = texto;
    b.setAttribute('aria-label', etiqueta);
    return b;
  }

  /* Crea (una sola vez) la barra de páginas después del marco de la tabla. */
  function barra(tbody) {
    var marco = tbody.closest('.sialss-mr__desplaza') || tbody.closest('table');
    var nav = marco.nextElementSibling;
    if (nav && nav.classList.contains('sialss-paginas')) { return nav; }
    nav = document.createElement('nav');
    nav.className = 'sialss-paginas';
    nav.setAttribute('aria-label', 'Páginas de la vista previa');
    nav.hidden = true;
    nav.innerHTML = '<span class="sialss-paginas__rango" aria-live="polite"></span><span class="sialss-paginas__botones"></span>';
    var caja = nav.querySelector('.sialss-paginas__botones');
    nav._primera = boton('« Primera', 'Primera página');
    nav._anterior = boton('‹ Anterior', 'Página anterior');
    nav._pagina = document.createElement('span');
    nav._pagina.className = 'sialss-paginas__actual';
    nav._siguiente = boton('Siguiente ›', 'Página siguiente');
    nav._ultima = boton('Última »', 'Última página');
    [nav._primera, nav._anterior, nav._pagina, nav._siguiente, nav._ultima].forEach(function (e) { caja.appendChild(e); });
    marco.parentNode.insertBefore(nav, marco.nextSibling);
    nav._primera.addEventListener('click', function () { nav._ir(0); });
    nav._anterior.addEventListener('click', function () { nav._ir(nav._actual - 1); });
    nav._siguiente.addEventListener('click', function () { nav._ir(nav._actual + 1); });
    nav._ultima.addEventListener('click', function () { nav._ir(nav._paginas - 1); });
    return nav;
  }

  function mostrar(tbody, filas, columnas, aCeldas, maximo) {
    var nav = barra(tbody);
    var marco = tbody.closest('.sialss-mr__desplaza');
    filas = filas || [];
    tbody.textContent = '';
    if (!filas.length) {
      var tr = document.createElement('tr'); var td = celda('Sin renglones válidos.');
      td.colSpan = columnas; td.className = 'sialss-mr__vacio'; tr.appendChild(td); tbody.appendChild(tr);
      nav.hidden = true;
      return;
    }
    nav._paginas = Math.ceil(filas.length / POR_PAGINA);
    nav._ir = function (p) {
      p = Math.max(0, Math.min(nav._paginas - 1, p));
      nav._actual = p;
      var desde = p * POR_PAGINA, hasta = Math.min(filas.length, desde + POR_PAGINA);
      tbody.textContent = '';
      for (var i = desde; i < hasta; i++) {
        var tr = document.createElement('tr');
        aCeldas(filas[i]).forEach(function (v) { tr.appendChild(celda(v)); });
        tbody.appendChild(tr);
      }
      nav.querySelector('.sialss-paginas__rango').textContent = 'Renglones ' + miles(desde + 1) + '–' + miles(hasta) + ' de '
        + miles(filas.length) + (maximo && filas.length >= maximo ? ' (la vista previa muestra los primeros ' + miles(maximo)
        + '; se cargan todos)' : '');
      nav._pagina.textContent = 'Página ' + miles(p + 1) + ' de ' + miles(nav._paginas);
      nav._primera.disabled = nav._anterior.disabled = p === 0;
      nav._siguiente.disabled = nav._ultima.disabled = p === nav._paginas - 1;
      if (marco) { marco.scrollTop = 0; }
    };
    nav.hidden = false;
    nav.querySelector('.sialss-paginas__botones').hidden = nav._paginas < 2;
    nav._ir(0);
  }

  window.SialssTablaPaginada = { mostrar: mostrar, POR_PAGINA: POR_PAGINA };
})();
