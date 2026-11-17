package mx.medalfa.sialss.manual;

import jakarta.servlet.ServletContext;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import mx.medalfa.sialss.seguridad.Acceso;

/**
 * Genera fuera del servidor un PDF del manual por cada perfil de cada acceso, igual al que entrega
 * /ManualUsuario?formato=pdf. Toma el menú de menu-roles.csv (sin base no hay permisos por usuario).
 * <p>
 * Uso: {@code java -cp <clases y librerías> mx.medalfa.sialss.manual.GenerarManualesPdf <carpeta Manuales> [salida]}
 * La salida por omisión es {@code <carpeta Manuales>/pdf}.
 */
public final class GenerarManualesPdf {

    private GenerarManualesPdf() {
    }

    public static void main(String[] args) throws Exception {
        Path manuales = Paths.get(args.length > 0 ? args[0] : "Manuales").toAbsolutePath().normalize();
        Path salida = args.length > 1 ? Paths.get(args[1]) : manuales.resolve("pdf");
        Files.createDirectories(salida);
        ServletContext ctx = contexto(manuales);
        int total = 0;
        for (Acceso acceso : Acceso.values()) {
            for (String perfil : ManualUsuario.perfiles(acceso)) {
                ManualUsuario.Manual m = ManualUsuario.para(ctx, acceso, perfil, null);
                String nombre = "Manual_usuario_SIALSS_MDF_" + acceso.name().toLowerCase() + "_perfil_" + perfil + ".pdf";
                try (OutputStream out = Files.newOutputStream(salida.resolve(nombre))) {
                    ManualPdf.escribir(m, null, out);
                }
                int opciones = m.capitulos().stream().mapToInt(c -> c.secciones().size()).sum();
                System.out.printf("%-9s perfil %-3s %2d módulos %3d opciones  %s%n", acceso, perfil,
                        m.capitulos().size(), opciones, nombre);
                total++;
            }
        }
        System.out.println(total + " PDF en " + salida);
    }

    /** ServletContext mínimo: solo getResourceAsStream sobre la carpeta Manuales (como WEB-INF/manuales). */
    private static ServletContext contexto(Path manuales) {
        return (ServletContext) Proxy.newProxyInstance(GenerarManualesPdf.class.getClassLoader(),
                new Class<?>[]{ServletContext.class}, (proxy, metodo, a) -> {
                    if ("getResourceAsStream".equals(metodo.getName())) {
                        String ruta = String.valueOf(a[0]);
                        if (ruta.startsWith(ManualUsuario.CARPETA)) {
                            Path p = manuales.resolve(ruta.substring(ManualUsuario.CARPETA.length()));
                            return Files.isRegularFile(p) ? (InputStream) Files.newInputStream(p) : null;
                        }
                        return null;
                    }
                    if ("toString".equals(metodo.getName())) {
                        return "ServletContext(" + manuales + ")";
                    }
                    return null;
                });
    }
}
