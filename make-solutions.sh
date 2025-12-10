#!/usr/bin/env bash

ls src/net/berkeley/peterseibel/Day*.java | sort | while read -r f; do basename "$f" .java; done
