SHELL		:= bash -O globstar
USERNAME	:= peterseibel
MAIN_CLASS	:= net.berkeley.$(USERNAME).AdventOfCode

solution_classes := $(wildcard src/net/berkeley/$(USERNAME)/Day*.java)
first_file := src/net/berkeley/$(USERNAME)/AdventOfCode.java
solutions := classes/net/berkeley/$(USERNAME)/solutions.txt

all: build run

build: | $(first_file) classes $(solutions)
	javac -d classes -Xdiags:verbose -Xlint:all -Xlint:-serial -Xlint:-this-escape src/**/*.java

$(solutions): make-solutions.sh $(solution_classes)
	mkdir -p $(dir $@)
	./make-solutions.sh > $@

run:
	java -cp classes -ea $(MAIN_CLASS)

run_all:
	java -cp classes $(MAIN_CLASS) --all

format:
	gjf src/**/*.java

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
