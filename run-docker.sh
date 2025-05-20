#!/bin/bash
# Script pro sestavení a spuštění Docker kontejnerů

# Výpis nápovědy
function show_help {
  echo "Použití: $0 [MOŽNOSTI]"
  echo "Sestaví a spustí Docker kontejnery pro Sky aplikaci"
  echo
  echo "Možnosti:"
  echo "  -e, --env [dev|prod]    Použije specifikované prostředí (výchozí: dev)"
  echo "  -b, --build             Sestaví kontejnery před spuštěním"
  echo "  -c, --clean             Vyčistí existující kontejnery a objemy"
  echo "  -h, --help              Zobrazí tuto nápovědu"
  echo
  echo "Příklady:"
  echo "  $0                      Spustí kontejnery v dev prostředí"
  echo "  $0 -e prod -b           Sestaví a spustí kontejnery v prod prostředí"
  echo "  $0 -c -b                Vyčistí, sestaví a spustí kontejnery v dev prostředí"
}

# Výchozí hodnoty
ENV="dev"
BUILD=false
CLEAN=false

# Zpracování parametrů
while [[ $# -gt 0 ]]; do
  case $1 in
    -e|--env)
      ENV="$2"
      shift 2
      ;;
    -b|--build)
      BUILD=true
      shift
      ;;
    -c|--clean)
      CLEAN=true
      shift
      ;;
    -h|--help)
      show_help
      exit 0
      ;;
    *)
      echo "Neznámá volba: $1"
      show_help
      exit 1
      ;;
  esac
done

# Kontrola prostředí
if [[ "$ENV" != "dev" && "$ENV" != "prod" ]]; then
  echo "Neplatné prostředí: $ENV. Použijte 'dev' nebo 'prod'."
  exit 1
fi

# Nastavení správného souboru docker-compose
if [[ "$ENV" == "prod" ]]; then
  COMPOSE_FILE="docker-compose.prod.yml"
  
  # Kontrola a nastavení proměnných prostředí pro produkci
  if [[ -z "$DB_USERNAME" || -z "$DB_PASSWORD" || -z "$JWT_SECRET" ]]; then
    echo "Varování: Některé proměnné prostředí nejsou nastaveny."
    echo "Pro produkční prostředí nastavte:"
    echo "  - DB_USERNAME"
    echo "  - DB_PASSWORD"
    echo "  - JWT_SECRET"
    
    # Nastavení výchozích hodnot pro demonstraci
    export DB_USERNAME=${DB_USERNAME:-postgres}
    export DB_PASSWORD=${DB_PASSWORD:-postgres}
    export JWT_SECRET=${JWT_SECRET:-defaultSecretKeyChangeMe}
    
    echo "Byly použity výchozí hodnoty - NEPOUŽÍVEJTE V REÁLNÉ PRODUKCI!"
  fi
else
  COMPOSE_FILE="docker-compose.yml"
fi

# Vyčištění
if [[ "$CLEAN" == true ]]; then
  echo "Čištění kontejnerů a objemů..."
  docker-compose -f $COMPOSE_FILE down -v
  docker-compose -f $COMPOSE_FILE rm -f
fi

# Sestavení a spuštění
if [[ "$BUILD" == true ]]; then
  echo "Sestavení a spuštění kontejnerů v prostředí $ENV..."
  docker-compose -f $COMPOSE_FILE up --build -d
else
  echo "Spuštění kontejnerů v prostředí $ENV..."
  docker-compose -f $COMPOSE_FILE up -d
fi

# Zobrazení stavů kontejnerů
echo "Stav kontejnerů:"
docker-compose -f $COMPOSE_FILE ps

echo "Logy aplikace můžete zobrazit pomocí: docker-compose -f $COMPOSE_FILE logs -f app"
