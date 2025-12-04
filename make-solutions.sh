#!/usr/bin/env bash

ls src/net/berkeley/peterseibel/Day0*.java | sort | while read -r f; do basename "$f" .java; done
