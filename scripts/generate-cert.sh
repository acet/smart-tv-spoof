#!/usr/bin/env bash
set -euo pipefail

OUT_DIR="${1:-.}"

openssl req -x509 \
  -newkey ec -pkeyopt ec_paramgen_curve:prime256v1 \
  -nodes \
  -days 3650 \
  -keyout "$OUT_DIR/samsung-spoof.key" \
  -out "$OUT_DIR/samsung-spoof.crt" \
  -subj '/CN=cdn.samsungcloudsolution.com' \
  -addext 'subjectAltName=DNS:cdn.samsungcloudsolution.com,DNS:time.samsungcloudsolution.com'

echo "Generated:"
openssl x509 -in "$OUT_DIR/samsung-spoof.crt" -noout -subject -ext subjectAltName