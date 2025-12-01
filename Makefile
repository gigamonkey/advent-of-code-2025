SHELL		:= bash -O globstar
USERNAME	:= peterseibel
MAIN_CLASS	:= net.berkeley.$(USERNAME).AdventOfCode

first_file := src/net/berkeley/$(USERNAME)/AdventOfCode.java

all: build run

build: | $(first_file) classes
	javac -d classes -Xdiags:verbose -Xlint:all -Xlint:-serial -Xlint:-this-escape src/**/*.java

run:
	java -cp classes $(MAIN_CLASS)

classes:
	mkdir $@

clean:
	rm -rf classes

$(first_file): | $(dir $(first_file))
	echo "package net.berkeley.$(USERNAME);" > $@
	echo "" >> $@
	echo "public class $$(basename $@ .java) {" >> $@
	echo "  public static void main(String[] args) {" >> $@
	echo "    System.out.println(\"Welcome to Advent of Code!\");" >> $@
	echo "  }" >> $@
	echo "}" >> $@

$(dir $(first_file)):
	mkdir -p $@
