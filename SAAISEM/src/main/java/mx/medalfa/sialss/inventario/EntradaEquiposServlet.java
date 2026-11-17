package mx.medalfa.sialss.inventario;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.medalfa.sialss.config.BaseDatos;

/**
 * Entrada de equipos (EntradaEquipos.jsp):
 * GET {@code accion=catalogos | insumos&texto= | recientes | cb} (JSON);
 * POST {@code accion=guardar} con los campos del formulario (JSON con folio y documento, o {@code error}).
 */
@WebServlet("/EntradaEquipos")
public class EntradaEquiposServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(EntradaEquiposServlet.class.getName());
    private static final Gson GSON = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (SesionAlmacen.de(req.getSession(false)) == null) {
            json(res, 401, Map.of("error", "Su sesión terminó o no es de Almacén. Vuelva a iniciar sesión."));
            return;
        }
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            EquipoDao dao = new EquipoDao(c);
            switch (String.valueOf(req.getParameter("accion"))) {
                case "catalogos" -> json(res, 200, dao.catalogos());
                case "insumos" -> json(res, 200, Map.of("insumos", dao.buscarInsumo(req.getParameter("texto"))));
                case "recientes" -> json(res, 200, Map.of("equipos", dao.recientes()));
                case "cb" -> json(res, 200, Map.of("cb", dao.nuevoCb()));
                default -> json(res, 400, Map.of("error", "Acción no válida."));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Entrada de equipos", e);
            json(res, 500, Map.of("error", "No se pudo consultar la base de datos."));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        SesionAlmacen s = SesionAlmacen.de(req.getSession(false));
        if (s == null) {
            json(res, 401, Map.of("error", "Su sesión terminó o no es de Almacén. Vuelva a iniciar sesión."));
            return;
        }
        if (!"guardar".equals(req.getParameter("accion"))) {
            json(res, 400, Map.of("error", "Acción no válida."));
            return;
        }
        EquipoDao.Equipo e;
        try {
            e = leer(req);
        } catch (EquipoDao.Rechazo r) {
            json(res, 400, Map.of("error", r.getMessage()));
            return;
        }
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            EquipoDao.Resultado r = new EquipoDao(c).guardar(e, s.usuario());
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("folio", r.folio());
            m.put("documento", r.documento());
            m.put("serie", r.serie());
            m.put("cb", r.cb());
            m.put("piezas", r.piezas());
            m.put("compra", r.compra());
            m.put("oc", r.ordenCompra());
            m.put("remision", r.remision());
            m.put("fechaCompra", r.fecha());
            json(res, 200, m);
        } catch (EquipoDao.Rechazo r) {
            json(res, 400, Map.of("error", r.getMessage()));
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Guardar equipo", ex);
            json(res, 500, Map.of("error", "No se pudo guardar el equipo: " + ex.getMessage()));
        }
    }

    private static EquipoDao.Equipo leer(HttpServletRequest r) throws EquipoDao.Rechazo {
        String clave = texto(r, "clave", 30);
        String serie = texto(r, "serie", 150);
        String ubicacion = texto(r, "ubicacion", 40);
        if (clave == null) {
            throw new EquipoDao.Rechazo("Busque y elija el equipo (clave o descripción).");
        }
        if (ubicacion == null) {
            throw new EquipoDao.Rechazo("Escriba la ubicación.");
        }
        Integer marca = entero(r, "marca");
        String marcaNueva = texto(r, "marcaNueva", 80);
        if (marca == null && marcaNueva == null) {
            throw new EquipoDao.Rechazo("Elija la marca o escriba una nueva.");
        }
        Integer proyecto = entero(r, "proyecto"), origen = entero(r, "origen"), proveedor = entero(r, "proveedor");
        if (proyecto == null || origen == null || proveedor == null) {
            throw new EquipoDao.Rechazo("Elija proyecto, origen y proveedor.");
        }
        BigDecimal costo = null;
        String c = texto(r, "costo", 20);
        if (c != null) {
            try {
                costo = new BigDecimal(c.replace(",", "").replace("$", ""));
            } catch (NumberFormatException ex) {
                throw new EquipoDao.Rechazo("El costo no es un número.");
            }
            if (costo.signum() < 0) {
                throw new EquipoDao.Rechazo("El costo no puede ser negativo.");
            }
        }
        String condicion = texto(r, "condicion", 30);
        if (condicion != null && !EquipoDao.CONDICIONES.contains(condicion)) {
            throw new EquipoDao.Rechazo("Condición no válida.");
        }
        LocalDate compra = fecha(r, "fechaCompra"), garantia = fecha(r, "garantiaHasta");
        if (compra != null && compra.isAfter(LocalDate.now())) {
            throw new EquipoDao.Rechazo("La fecha de compra no puede ser futura.");
        }
        if (compra != null && garantia != null && garantia.isBefore(compra)) {
            throw new EquipoDao.Rechazo("La garantía no puede terminar antes de la fecha de compra.");
        }
        return new EquipoDao.Equipo(clave, marca, marcaNueva, entero(r, "modelo"), texto(r, "modeloNuevo", 80),
                serie, texto(r, "cb", 255), texto(r, "categoria", 80), costo, condicion, compra, garantia,
                texto(r, "accesorios", 500), texto(r, "notas", 1000), proyecto, origen, proveedor,
                ubicacion.toUpperCase(), texto(r, "tarima", 100), texto(r, "remision", 40), texto(r, "ordenCompra", 40),
                empaque(r));
    }

    /** Tarimas, cajas y piezas como en la Entrada Manual; todo vacío = 1 pieza. */
    private static EquipoDao.Empaque empaque(HttpServletRequest r) throws EquipoDao.Rechazo {
        int tc = cantidad(r, "tarimasC", "tarimas completas"), cxt = cantidad(r, "cajasXTarima", "cajas por tarima"),
                pxc = cantidad(r, "piezasXCaja", "piezas por caja"), ti = cantidad(r, "tarimasI", "tarimas incompletas"),
                ci = cantidad(r, "cajasI", "cajas de las tarimas incompletas"), resto = cantidad(r, "resto", "resto");
        long piezas = ((long) tc * cxt + ci) * pxc + resto;
        if (piezas > 10_000_000L) {
            throw new EquipoDao.Rechazo("La entrada pasa de 10,000,000 de piezas: revise tarimas, cajas y piezas.");
        }
        return new EquipoDao.Empaque(tc, cxt, pxc, ti, ci, resto);
    }

    private static int cantidad(HttpServletRequest r, String nombre, String etiqueta) throws EquipoDao.Rechazo {
        String v = texto(r, nombre, 9);
        if (v == null) {
            return 0;
        }
        v = v.replace(",", "");
        if (!v.matches("\\d{1,7}")) {
            throw new EquipoDao.Rechazo("En " + etiqueta + " escriba un número entero de 0 a 9,999,999.");
        }
        return Integer.parseInt(v);
    }

    private static String texto(HttpServletRequest r, String nombre, int maximo) throws EquipoDao.Rechazo {
        String v = r.getParameter(nombre);
        if (v == null || v.isBlank()) {
            return null;
        }
        v = v.trim();
        if (v.length() > maximo) {
            throw new EquipoDao.Rechazo("El campo " + nombre + " admite hasta " + maximo + " caracteres.");
        }
        return v;
    }

    private static Integer entero(HttpServletRequest r, String nombre) throws EquipoDao.Rechazo {
        String v = texto(r, nombre, 12);
        if (v == null) {
            return null;
        }
        try {
            return Integer.valueOf(v);
        } catch (NumberFormatException ex) {
            throw new EquipoDao.Rechazo("Valor no válido en " + nombre + ".");
        }
    }

    private static LocalDate fecha(HttpServletRequest r, String nombre) throws EquipoDao.Rechazo {
        String v = texto(r, nombre, 10);
        if (v == null) {
            return null;
        }
        try {
            return LocalDate.parse(v);
        } catch (DateTimeParseException ex) {
            throw new EquipoDao.Rechazo("Fecha no válida en " + nombre + ".");
        }
    }

    private static void json(HttpServletResponse res, int estado, Object cuerpo) throws IOException {
        res.setStatus(estado);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-store");
        res.getWriter().write(GSON.toJson(cuerpo));
    }
}
