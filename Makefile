SHELL		:= bash -O globstar
USERNAME	:= $(error Set the USERNAME variable in the Makefile to the part of your student email before the @)
MAIN_CLASS	:= net.berkeley.students.$(USERNAME).AdventOfCode

first_file := src/net/berkeley/students/$(USERNAME)/AdventOfCode.java

all: build run

build: | $(first_file) classes
	javac -d classes -Xdiags:verbose -Xlint:all src/**/*.java

run:
	java -cp classes $(MAIN_CLASS)

classes:
	mkdir $@

clean:
	rm -rf classes

$(first_file): | $(dir $(first_file))
	echo "package net.berkeley.students.$(USERNAME);" > $@
	echo "" >> $@
	echo "public class $$(basename $@ .java) {" >> $@
	echo "  public static void main(String[] args) {" >> $@
	echo "    System.out.println(\"Welcome to Advent of Code!\");" >> $@
	echo "  }" >> $@
	echo "}" >> $@

$(dir $(first_file)):
	mkdir -p $@
