#! /bin/bash

OUTPUT_DIR='test-out'

for filename in "$OUTPUT_DIR"/* ; do
    basename "$filename"
    grep -E "${1:-.*}" < "$filename"
    echo
done
