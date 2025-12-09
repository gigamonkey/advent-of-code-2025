#!/usr/bin/env bash

set -euo pipefail

day=$1
name=$2

inputs=$(printf "inputs/day-%02d" $day)
class=$(printf "Day%02d_%s" $day $name)
file="src/net/berkeley/peterseibel/$class.java"

if [[ -e "$file" ]]; then
    echo "File already exists!"
    exit 1
fi

mkdir -p "$inputs"
touch "$inputs/test.data"
touch "$inputs/test.part1.expected"

cat > "$file" <<EOF;
package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;

import module java.base;

public class $class extends Solution<List<String>, Long> {

  public $class() {
    super($day, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    long count = 0;
    return count;
  }

  public Long part2(List<String> lines) {
    long count = 0;
    return count;
  }
}
EOF

# Echo files so I can jump to them from terminal buffer
echo "$inputs/test.data"
echo "$inputs/test.part1.expected"
echo "$file"
