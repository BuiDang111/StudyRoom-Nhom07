#!/bin/sh
cd "$(dirname "$0")/runtime" || exit 1
exec java -jar studyroom.jar
