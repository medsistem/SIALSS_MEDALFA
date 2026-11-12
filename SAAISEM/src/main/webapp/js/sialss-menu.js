/* SIALSS: comportamiento de la barra superior y del menú lateral (sin dependencias).
   El menú es el HTML de jspf/menuPrincipal*.jspf; aquí solo se abre y cierra por grupos. */
(function () {
  'use strict';

  var CLAVE_OCULTO = 'sialss.menuOculto';

  function guardar(clave, valor) {
    try { localStorage.setItem(clave, valor); } catch (e) { /* sin almacenamiento: no pasa nada */ }
  }
  function leer(clave) {
    try { return localStorage.getItem(clave); } catch (e) { return null; }
  }

  function iniciar() {
    var menu = document.getElementById('sialss-menu');
    var body = document.body;
    if (!menu) { return; }

    // El menú puede venir dentro de un .container de la página: se saca al cuerpo para fijarlo a la izquierda.
    if (menu.parentNode !== body) { body.insertBefore(menu, body.firstChild); }
    var barra = document.getElementById('sialss-barra');
    if (barra && barra.parentNode !== body) { body.insertBefore(barra, body.firstChild); }
    body.classList.add('sialss-app');

    var angosta = window.matchMedia('(max-width: 900px)');
    if (!angosta.matches && leer(CLAVE_OCULTO) === '1') { body.classList.add('sialss-menu-oculto'); }

    // Grupos: el clic abre/cierra el grupo (en lugar del desplegable de Bootstrap 3).
    var grupos = menu.querySelectorAll('.nav.navbar-nav > li.dropdown');
    Array.prototype.forEach.call(grupos, function (li) {
      var a = li.querySelector(':scope > a.dropdown-toggle');
      if (!a) { return; }
      a.removeAttribute('data-toggle');
      a.setAttribute('role', 'button');
      a.setAttribute('aria-expanded', 'false');
      a.addEventListener('click', function (ev) {
        ev.preventDefault();
        ev.stopPropagation();
        var abrir = !li.classList.contains('abierto');
        li.classList.toggle('abierto', abrir);
        a.setAttribute('aria-expanded', abrir ? 'true' : 'false');
      });
    });

    // Opción de la página actual: se marca y su grupo queda abierto.
    var aqui = location.pathname + location.search;
    var enlaces = menu.querySelectorAll('.dropdown-menu a[href]');
    Array.prototype.forEach.call(enlaces, function (a) {
      var href = (a.getAttribute('href') || '').trim();
      // "#" o "javascript:" apuntan a la misma página: no son la opción actual.
      if (!href || href.charAt(0) === '#' || href.toLowerCase().indexOf('javascript:') === 0) { return; }
      var destino = a.pathname + a.search;
      if (destino === aqui || (a.search === '' && a.pathname === location.pathname)) {
        a.setAttribute('aria-current', 'page');
        var li = a.closest('li.dropdown');
        if (li) {
          li.classList.add('abierto', 'activo');
          var t = li.querySelector(':scope > a.dropdown-toggle');
          if (t) { t.setAttribute('aria-expanded', 'true'); }
        }
      }
    });

    // Botón de la barra: oculta o muestra el menú.
    var alternar = document.getElementById('sialss-alternar-menu');
    if (alternar) {
      var actualizarBoton = function () {
        var visible = angosta.matches ? body.classList.contains('sialss-menu-abierto') : !body.classList.contains('sialss-menu-oculto');
        alternar.setAttribute('aria-expanded', visible ? 'true' : 'false');
        var t = document.getElementById('sialss-alternar-texto');
        if (t) { t.textContent = visible ? 'Ocultar menú' : 'Mostrar menú'; }
      };
      alternar.addEventListener('click', function () {
        if (angosta.matches) {
          body.classList.toggle('sialss-menu-abierto');
        } else {
          var oculto = body.classList.toggle('sialss-menu-oculto');
          guardar(CLAVE_OCULTO, oculto ? '1' : '0');
        }
        actualizarBoton();
      });
      actualizarBoton();
    }

    // Menú del usuario.
    var botonUsuario = document.getElementById('sialss-boton-usuario');
    var desplegable = document.getElementById('sialss-menu-usuario');
    if (botonUsuario && desplegable) {
      botonUsuario.addEventListener('click', function (ev) {
        ev.stopPropagation();
        var abrir = desplegable.hasAttribute('hidden');
        desplegable.toggleAttribute('hidden', !abrir);
        botonUsuario.setAttribute('aria-expanded', abrir ? 'true' : 'false');
      });
      document.addEventListener('click', function (ev) {
        if (!desplegable.hasAttribute('hidden') && !desplegable.contains(ev.target)) {
          desplegable.setAttribute('hidden', '');
          botonUsuario.setAttribute('aria-expanded', 'false');
        }
      });
      document.addEventListener('keydown', function (ev) {
        if (ev.key === 'Escape' && !desplegable.hasAttribute('hidden')) {
          desplegable.setAttribute('hidden', '');
          botonUsuario.setAttribute('aria-expanded', 'false');
          botonUsuario.focus();
        }
      });
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', iniciar);
  } else {
    iniciar();
  }
})();
