#!/bin/sh
set -eu
cd "$(dirname "$0")"
command -v java >/dev/null
if [ ! -f runtime/studyroom.jar ]; then
    printf '%s\n' 'Build first: sh BUILD_UNIX.sh' >&2
    exit 1
fi
for name in students rooms bookings; do
    if [ ! -f "runtime/data/$name.json" ]; then
        printf '%s\n' 'Runtime data is incomplete. Run: sh BUILD_UNIX.sh' >&2
        exit 1
    fi
done
cd runtime
printf '%s\n' 'Open http://localhost:8080 after the application starts.'
exec java -jar studyroom.jar "$@"
