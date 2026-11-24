#!/bin/sh
set -eu
cd "$(dirname "$0")"
command -v java >/dev/null
command -v javac >/dev/null
command -v npm >/dev/null
(cd frontend && npm ci && npm run build)
mkdir -p backend/src/main/resources/static
cp -R frontend/dist/. backend/src/main/resources/static/
(cd backend && sh mvnw package)
mkdir -p runtime/data
cp backend/target/room-booking-1.0.0.jar runtime/studyroom.jar
for name in students rooms bookings; do
    if [ ! -f "runtime/data/$name.json" ]; then
        cp "backend/data/$name.json" "runtime/data/$name.json"
    fi
done
printf '%s\n' 'Build complete. Run: sh START_UNIX.sh. Existing runtime data is preserved.'
