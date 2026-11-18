#!/bin/sh
# Corre el agente manual-usuario-sialss sobre los commits que aún no están en el manual.
# Lo llama .githooks/post-commit; también se puede correr a mano:  sh .githooks/generar-manual.sh

RAIZ=$(git rev-parse --show-toplevel) || exit 1
cd "$RAIZ" || exit 1
CANDADO="$RAIZ/.git/manual-usuario.lock"
ESTADO="$RAIZ/SAAISEM/Manuales/.ultimo_commit"

# Una sola ejecución a la vez; si llega otro commit mientras corre, el ciclo de abajo lo alcanza.
if ! mkdir "$CANDADO" 2>/dev/null; then
  echo "$(date '+%F %T') ya hay una generación en curso; se incluirá este commit al terminar."
  exit 0
fi
trap 'rmdir "$CANDADO" 2>/dev/null' EXIT INT TERM

# Ubicar el CLI de Claude: primero el PATH, si no el más reciente que instala la app de escritorio.
CLAUDE=$(command -v claude 2>/dev/null)
if [ -z "$CLAUDE" ]; then
  BASE=$(cygpath -u "${LOCALAPPDATA:-$HOME/AppData/Local}" 2>/dev/null || echo "$HOME/AppData/Local")
  CLAUDE=$(ls -t "$BASE"/Claude-Data/*/claude-code/*/*/claude.exe 2>/dev/null | head -1)
fi
if [ -z "$CLAUDE" ]; then
  echo "$(date '+%F %T') ERROR: no encontré el CLI de Claude (claude). Instálalo o agrégalo al PATH."
  exit 1
fi

INTENTOS=0
ACTUALIZADO=0
while [ $INTENTOS -lt 3 ]; do
  INTENTOS=$((INTENTOS + 1))
  HEAD_ACTUAL=$(git rev-parse HEAD)
  ULTIMO=$(cat "$ESTADO" 2>/dev/null | tr -d '[:space:]')
  [ "$ULTIMO" = "$HEAD_ACTUAL" ] && break

  if [ -n "$ULTIMO" ] && git cat-file -e "$ULTIMO^{commit}" 2>/dev/null; then
    ARCHIVOS=$(git diff --name-only "$ULTIMO" "$HEAD_ACTUAL" | grep -vE '^(SAAISEM/Manuales/|\.githooks/|\.claude/)' | tr '\n' ' ')
    PEDIDO="Modo incremental. Actualiza el manual de usuario SOLO en los módulos afectados por el rango $ULTIMO..$HEAD_ACTUAL. Archivos cambiados: $ARCHIVOS. No toques los demás módulos. Al terminar escribe $HEAD_ACTUAL en SAAISEM/Manuales/.ultimo_commit."
  else
    PEDIDO="Modo completo. Genera el manual de usuario completo de SIALSS_MDF. Al terminar escribe $HEAD_ACTUAL en SAAISEM/Manuales/.ultimo_commit."
  fi

  echo "$(date '+%F %T') inicio ($HEAD_ACTUAL) — $PEDIDO"
  "$CLAUDE" -p "$PEDIDO" \
    --agent manual-usuario-sialss \
    --permission-mode acceptEdits \
    --allowedTools "Read" "Glob" "Grep" "Write" "Edit" \
      "Bash(git log:*)" "Bash(git show:*)" "Bash(git diff:*)" "Bash(git rev-parse:*)" \
    --disallowedTools "Bash(git add:*)" "Bash(git commit:*)" "Bash(git push:*)" "Bash(git reset:*)" "Bash(git checkout:*)"
  RC=$?
  echo "$(date '+%F %T') fin (código $RC)"
  [ $RC -ne 0 ] && exit $RC
  ACTUALIZADO=1
done

# Con el manual al día, un PDF por perfil en SAAISEM/Manuales/pdf.
[ $ACTUALIZADO -eq 1 ] && sh "$RAIZ/.githooks/generar-pdf-manual.sh"
exit 0
