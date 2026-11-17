package mx.medalfa.sialss.cargas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;
import mx.medalfa.sialss.seguridad.Acceso;

/**
 * Carga de órdenes de compra (reemplaza cargarecibo.jsp → FileUploadServletRecibo → LeeExcelRecibo).
 * Columnas como el archivo de siempre: Orden de compra, Proveedor (F_ClaProve), Nombre del proveedor (solo de
 * referencia), Clave, Cantidad, Proyecto, Fuente de financiamiento (opcional, "Pendiente" si va vacía).
 * <p>
 * Cada combinación orden + proveedor + proyecto del archivo reemplaza lo que ya hubiera en tb_pedido_sialss para
 * esa orden (como antes), salvo que ya tenga piezas recibidas: entonces es error. Se guarda confirmada
 * (F_StsPed 1), entrega a 7 días, moneda 1 (MXM, F_Tipo como la captura de OC) y el usuario de compras
 * (tb_usuariocompra) de quien carga. El tipo "LP" / "CC/LP" de la carga anterior ya no tiene columna (F_Tipo ahora es
 * la moneda) y no se guarda; la observación "LPN-44064003-030-20" de las órdenes CCISEM/OCISEM-AR-2020 sí.
 * Diferencias: antes se insertaba un renglón por fila del Excel; ahora se suman las filas repetidas de la misma
 * clave. El INSERT lleva nombres de columna (el anterior fallaba porque tb_pedido_sialss ya tiene más columnas).
 */
public final class CargaOC implements CargaArchivo {

    static final String[] COLUMNAS = {"Orden de compra", "Proveedor", "Nombre del proveedor", "Clave", "Cantidad",
            "Proyecto", "Fuente de financiamiento"};

    record Renglon(int numero, String orden, String proveedor, String clave, String claveSS, int cantidad, int proyecto,
                   String fuente) implements Serializable {
    }

    record Datos(List<Renglon> renglones, int idUsuarioCompra) implements Serializable {
    }

    @Override
    public String nombre() {
        return "oc";
    }

    @Override
    public String pagina() {
        return "CargaArchivoOC.jsp";
    }

    @Override
    public Set<Acceso> accesos() {
        return Set.of(Acceso.ALMACEN, Acceso.COMPRAS);
    }

    @Override
    public void plantilla(Connection c, OutputStream salida) throws IOException, SQLException {
        Excel.plantilla(salida, "OC", COLUMNAS, 2,
                new String[][]{{"OC-2026-001", "90004", "PROVEEDOR DE EJEMPLO", "200962.02", "100", "1", "Pendiente"}},
                List.of(new Excel.Bloque("Proveedor (columna B)", Consultas.lista(c, "SELECT F_ClaProve, F_NomPro FROM tb_proveedor ORDER BY F_ClaProve")),
                        new Excel.Bloque("Proyecto (columna F)", Consultas.lista(c, "SELECT F_Id, F_DesProy FROM tb_proyectos ORDER BY F_Id"))),
                List.of("Carga de órdenes de compra SIALSS. Un renglón por orden y clave.",
                        "Obligatorias: Orden de compra, Proveedor, Clave, Cantidad y Proyecto. El nombre del proveedor es solo de referencia.",
                        "Proveedor y Proyecto: el número de la hoja Catálogos. La clave debe existir y no estar suspendida.",
                        "Fuente de financiamiento: opcional; si va vacía se guarda \"Pendiente\".",
                        "Si la orden ya estaba cargada para ese proveedor y proyecto, se reemplaza (no se puede si ya tiene piezas recibidas).",
                        "Máximo " + Excel.MAXIMO_RENGLONES + " renglones. Si un renglón tiene error no se carga nada: corrija el archivo y vuelva a subirlo."));
    }

    @Override
    public Revision revisar(Connection c, InputStream archivo, Usuario u) throws IOException, SQLException {
        Integer idCompra = idUsuarioCompra(c, u);
        if (idCompra == null) {
            return Revision.conError("Su usuario (" + u.usuario() + ") no está dado de alta en los usuarios de compras "
                    + "(tb_usuariocompra), que es donde la orden guarda quién la capturó. Pida al administrador que lo dé de alta.");
        }
        List<Excel.Renglon> crudos;
        try {
            crudos = Excel.leer(archivo, COLUMNAS.length, Set.of());
        } catch (Excel.Ilegible e) {
            return Revision.conError(e.getMessage());
        }
        Map<String, String> proveedores = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_ClaProve, F_NomPro FROM tb_proveedor")) {
            proveedores.put(f.get(1), f.get(2));
        }
        Map<String, Fila> insumos = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_ClaPro, F_DesPro, F_StsPro, F_ClaProSS FROM tb_medica")) {
            insumos.put(f.get(1), f);
        }
        Map<String, String> proyectos = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_Id, F_DesProy FROM tb_proyectos")) {
            proyectos.put(f.get(1), f.get(2));
        }
        List<String> errores = new ArrayList<>();
        List<Renglon> renglones = new ArrayList<>();
        List<List<String>> muestra = new ArrayList<>();
        long piezas = 0;
        for (Excel.Renglon cr : crudos) {
            String[] v = cr.v();
            List<String> mal = new ArrayList<>();
            String orden = v[0].replace(" ", "");
            if (orden.isEmpty()) {
                mal.add("falta la orden de compra");
            } else if (orden.length() > 50) {
                mal.add("la orden de compra pasa de 50 caracteres");
            }
            String proveedor = Excel.sinDecimalCero(v[1].replace(" ", ""));
            if (proveedor.isEmpty()) {
                mal.add("falta el proveedor");
            } else if (!proveedores.containsKey(proveedor)) {
                mal.add("el proveedor " + proveedor + " no existe");
            }
            String clave = Claves.resolver(v[3], insumos);
            Fila ins = clave == null ? null : insumos.get(clave);
            if (v[3].isEmpty()) {
                mal.add("falta la clave");
            } else if (ins == null) {
                mal.add("la clave " + v[3] + " no existe");
            } else if ("S".equals(ins.get(3))) {
                mal.add("la clave " + clave + " está suspendida");
            }
            int cantidad = Excel.cantidad(v[4], "la cantidad", mal);
            String proyecto = Excel.sinDecimalCero(v[5]);
            if (proyecto.isEmpty()) {
                mal.add("falta el proyecto");
            } else if (!proyectos.containsKey(proyecto)) {
                mal.add("el proyecto " + proyecto + " no existe");
            }
            String fuente = v[6].isEmpty() ? "Pendiente" : v[6];
            if (fuente.length() > 50) {
                mal.add("la fuente de financiamiento pasa de 50 caracteres");
            }
            if (!mal.isEmpty()) {
                errores.add("Renglón " + cr.numero() + ": " + String.join("; ", mal) + ".");
                continue;
            }
            String claveSS = ins.get(4) == null || ins.get(4).isEmpty() ? clave : ins.get(4);
            renglones.add(new Renglon(cr.numero(), orden, proveedor, clave, claveSS, cantidad, Integer.parseInt(proyecto), fuente));
            piezas += cantidad;
            if (muestra.size() < 200) {
                muestra.add(List.of(String.valueOf(cr.numero()), orden, proveedor + " · " + proveedores.get(proveedor), clave,
                        ins.get(2), String.valueOf(cantidad), proyecto + " · " + proyectos.get(proyecto), fuente));
            }
        }
        // Órdenes que ya existen: se reemplazan, salvo que ya tengan algo recibido.
        Map<String, List<Renglon>> grupos = agrupar(renglones);
        int reemplazos = 0;
        for (List<Renglon> g : grupos.values()) {
            Renglon x = g.get(0);
            List<Fila> previas = Consultas.lista(c,
                    "SELECT COUNT(*), COALESCE(SUM(F_Recibido <> 0), 0) FROM tb_pedido_sialss WHERE F_NoCompra = ? AND F_Provee = ? AND F_Proyecto = ?",
                    x.orden(), x.proveedor(), x.proyecto());
            int existentes = previas.get(0).entero(1);
            if (previas.get(0).entero(2) > 0) {
                errores.add("La orden " + x.orden() + " (proveedor " + x.proveedor() + ", proyecto " + x.proyecto()
                        + ") ya tiene piezas recibidas: no se puede reemplazar por carga.");
            } else if (existentes > 0) {
                reemplazos++;
            }
        }
        if (!errores.isEmpty()) {
            return new Revision(errores, muestra, "", null);
        }
        String resumen = Texto.cuantos(grupos.size(), "orden de compra", "órdenes de compra") + ", "
                + Texto.cuantos(renglones.size(), "renglón", "renglones") + ", " + Texto.cuantos(piezas, "pieza", "piezas")
                + (reemplazos == 0 ? "." : "; " + Texto.cuantos(reemplazos, "orden ya estaba cargada y se reemplazará",
                "órdenes ya estaban cargadas y se reemplazarán") + ".");
        return new Revision(List.of(), muestra, resumen, new Datos(new ArrayList<>(renglones), idCompra));
    }

    @Override
    public String aplicar(Connection c, Revision r, Usuario u) throws SQLException {
        Datos d = (Datos) r.datos();
        Map<String, List<Renglon>> grupos = agrupar(d.renglones());
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        int borrados = 0;
        int insertados = 0;
        try (PreparedStatement borrar = c.prepareStatement(
                     "DELETE FROM tb_pedido_sialss WHERE F_NoCompra = ? AND F_Provee = ? AND F_Proyecto = ? AND F_Recibido = 0");
             PreparedStatement recibidos = c.prepareStatement(
                     "SELECT COUNT(*) FROM tb_pedido_sialss WHERE F_NoCompra = ? AND F_Provee = ? AND F_Proyecto = ? AND F_Recibido <> 0 FOR UPDATE");
             PreparedStatement alta = c.prepareStatement(
                     "INSERT INTO tb_pedido_sialss (F_NoCompra, F_Provee, F_Clave, F_ClaveSS, F_CodBar, F_Priori, F_Lote, F_Cadu, "
                     + "F_Cant, F_Obser, F_Fecha, F_FecSur, F_HorSur, F_IdUsu, F_StsPed, F_Recibido, F_Proyecto, Fecha_Vencimiento, "
                     + "Plazo_Pago, F_CostoUni, F_Precio, F_Tipo, F_IdOrigen, F_FuenteFinanza, F_Contratos) "
                     + "VALUES (?, ?, ?, ?, '-', '1', '-', CURDATE(), ?, ?, NOW(), DATE_ADD(CURDATE(), INTERVAL 7 DAY), CURTIME(), ?, 1, 0, ?, "
                     + "DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'N/A', 0, 0, '1', 0, ?, '')")) {
            for (List<Renglon> g : grupos.values()) {
                Renglon x = g.get(0);
                recibidos.setString(1, x.orden());
                recibidos.setString(2, x.proveedor());
                recibidos.setInt(3, x.proyecto());
                try (var rs = recibidos.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new SQLException("La orden " + x.orden() + " recibió piezas mientras se revisaba el archivo.");
                    }
                }
                borrar.setString(1, x.orden());
                borrar.setString(2, x.proveedor());
                borrar.setInt(3, x.proyecto());
                borrados += borrar.executeUpdate();
                // Una fila por clave: se suman las repetidas; la fuente es la primera que aparezca.
                Map<String, Renglon> porClave = new LinkedHashMap<>();
                for (Renglon y : g) {
                    porClave.merge(y.clave(), y, (a, b) -> new Renglon(a.numero(), a.orden(), a.proveedor(), a.clave(),
                            a.claveSS(), a.cantidad() + b.cantidad(), a.proyecto(), a.fuente()));
                }
                boolean ar = x.orden().contains("CCISEM-AR-2020-") || x.orden().contains("OCISEM-AR-2020-");
                for (Renglon y : porClave.values()) {
                    alta.setString(1, y.orden());
                    alta.setString(2, y.proveedor());
                    alta.setString(3, y.clave());
                    alta.setString(4, y.claveSS());
                    alta.setString(5, String.valueOf(y.cantidad()));
                    alta.setString(6, ar ? "LPN-44064003-030-20" : "-");
                    alta.setString(7, String.valueOf(d.idUsuarioCompra()));
                    alta.setInt(8, y.proyecto());
                    alta.setString(9, y.fuente());
                    alta.addBatch();
                    insertados++;
                }
            }
            alta.executeBatch();
            c.commit();
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(auto);
        }
        return (grupos.size() == 1 ? "Se cargó " : "Se cargaron ") + Texto.cuantos(grupos.size(), "orden de compra", "órdenes de compra") + " con "
                + Texto.cuantos(insertados, "clave", "claves") + (borrados == 0 ? "." : "; se reemplazaron "
                + Texto.cuantos(borrados, "renglón anterior", "renglones anteriores") + ".")
                + " Quedan confirmadas, con entrega a 7 días.";
    }

    private static Map<String, List<Renglon>> agrupar(List<Renglon> renglones) {
        Map<String, List<Renglon>> g = new LinkedHashMap<>();
        for (Renglon x : renglones) {
            g.computeIfAbsent(x.orden() + "|" + x.proveedor() + "|" + x.proyecto(), k -> new ArrayList<>()).add(x);
        }
        return g;
    }

    /** tb_pedido_sialss.F_IdUsu es el id de tb_usuariocompra. En Compras es el de la sesión; en Almacén, por nombre. */
    private static Integer idUsuarioCompra(Connection c, Usuario u) throws SQLException {
        if (u.acceso() == Acceso.COMPRAS) {
            return u.idUsuario();
        }
        List<Fila> f = Consultas.lista(c,
                "SELECT F_IdUsu FROM tb_usuariocompra WHERE F_Usu = ? OR F_Nombre = ? ORDER BY F_Usu = ? DESC LIMIT 1",
                u.usuario(), u.usuario(), u.usuario());
        return f.isEmpty() ? null : f.get(0).entero(1);
    }
}
