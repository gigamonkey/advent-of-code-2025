#!/usr/bin/env bash

set -euo pipefail

day=$1
name=$2


inputs=$(printf "inputs/day-%02d/" $day)
class=$(printf "Day%02d_%s" $day $name)
file="src/net/berkeley/peterseibel/$class.java"

if [[ -e "$file" ]]; then
    echo "File already exists!"
    exit 1
fi

echo "Making inputs dir $inputs"
mkdir -p "$inputs"
touch "$inputs/test.txt"
touch "$inputs/test.part1.expected"

echo "Saving to $file"

cat > "$file" <<EOF;
package net.berkeley.peterseibel;

import static java.lang.Math.*;

import module java.base;

public class $class extends Solution<List<String>, Long> {

  public $class() {
    super($day, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> banks) {
    return 0L;
  }

  public Long part2(List<String> banks) {
    return 0L;
  }
}
EOF
