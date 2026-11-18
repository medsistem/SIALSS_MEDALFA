#!/bin/sh
# Genera un PDF del manual por cada perfil en SAAISEM/Manuales/pdf (iText 2.1.7, mismo código que
# /ManualUsuario?formato=pdf). Lo llama generar-manual.sh al terminar el agente; también se puede
# correr a mano:  sh .githooks/generar-pdf-manual.sh
# No usa Maven: compila solo el paquete mx.medalfa.sialss.manual con un JDK 17 en .git/manual-pdf-clases.

RAIZ=$(git rev-parse --show-toplevel) || exit 1
APP="$RAIZ/SAAISEM"
CLASES="$RAIZ/.git/manual-pdf-clases"
w() { cygpath -w "$1" 2>/dev/null || echo "$1"; }

# JDK 17 o mayor: JAVA_HOME si sirve, si no el que esté instalado en Program Files.
JDK=""
for j in "$JAVA_HOME" "/c/Program Files/Java/jdk-17"* "/c/Program Files/Eclipse Adoptium/jdk-17"* \
         "/c/Program Files/Java/jdk-2"* "/c/Program Files/Eclipse Adoptium/jdk-2"*; do
  [ -n "$j" ] && [ -x "$j/bin/javac" -o -x "$j/bin/javac.exe" ] || continue
  v=$("$j/bin/java" -version 2>&1 | head -1 | sed -E 's/.*"([0-9]+).*/\1/')
  [ "${v:-0}" -ge 17 ] 2>/dev/null && { JDK="$j"; break; }
done
if [ -z "$JDK" ]; then
  echo "$(date '+%F %T') PDF: no encontré un JDK 17; no se generaron los PDF."
  exit 1
fi

# Librerías: servlet-api e iText del repositorio de Maven; las del WAR si ya se compiló alguna vez.
M2="$HOME/.m2/repository"
SERVLET=$(ls "$M2"/jakarta/servlet/jakarta.servlet-api/*/jakarta.servlet-api-*.jar 2>/dev/null | grep -v sources | sort | tail -1)
ITEXT=$(ls "$M2"/com/lowagie/itext/2.1.7/itext-2.1.7.jar 2>/dev/null | head -1)
[ -z "$ITEXT" ] && ITEXT=$(ls "$APP"/target/*/WEB-INF/lib/itext-2.1.7.jar 2>/dev/null | head -1)
if [ -z "$SERVLET" ] || [ -z "$ITEXT" ]; then
  echo "$(date '+%F %T') PDF: faltan jakarta.servlet-api o itext-2.1.7 (compile el proyecto una vez con Maven)."
  exit 1
fi
CP="$(w "$SERVLET");$(w "$ITEXT")"
# Provided de Tomcat que piden las clases de configuración (pool de conexiones y su bitácora).
for a in tomcat-dbcp tomcat-juli; do
  j=$(ls "$M2"/org/apache/tomcat/$a/*/$a-*.jar 2>/dev/null | grep -v sources | sort | tail -1)
  [ -n "$j" ] && CP="$CP;$(w "$j")"
done
for d in "$APP"/target/*/WEB-INF/lib; do [ -d "$d" ] && CP="$CP;$(w "$d")/*"; done

rm -rf "$CLASES" && mkdir -p "$CLASES"
if ! "$JDK/bin/javac" -encoding UTF-8 -nowarn -proc:none -implicit:class -d "$(w "$CLASES")" -cp "$CP" \
      -sourcepath "$(w "$APP/src/main/java")" "$(w "$APP/src/main/java/mx/medalfa/sialss/manual")"/*.java; then
  echo "$(date '+%F %T') PDF: no compiló el paquete del manual; no se generaron los PDF."
  exit 1
fi
"$JDK/bin/java" -Dfile.encoding=UTF-8 -Djava.util.logging.config.file=NUL \
  -cp "$(w "$CLASES");$(w "$APP/src/main/resources");$CP" \
  mx.medalfa.sialss.manual.GenerarManualesPdf "$(w "$APP/Manuales")"
RC=$?
echo "$(date '+%F %T') PDF: fin (código $RC)"
exit $RC
