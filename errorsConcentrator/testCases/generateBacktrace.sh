#!/bin/bash

BACKTRACE_NUMBER=$1
BACKTRACE_FILE=backtrace.txt
PACKAGE_FILE=package.txt
VERBS_FILE=verbs.txt
NOUNS_FILE=nouns.txt
rm $BACKTRACE_FILE

for((i=0; i<=$BACKTRACE_NUMBER; i++)); do
	reg=""
	BACKTRACE_LINES=$(( ( RANDOM % 20 )  + 1 ))
	THREAD_LINE=$(( ( RANDOM % 4554 )  + 1 ))
	THREAD_PACKAGE_LINE=$(( ( RANDOM % 110 )  + 1 ))
	THREAD=$(sed "${THREAD_LINE}q;d" $NOUNS_FILE)
	PACKAGE_THREAD=$(sed "${THREAD_PACKAGE_LINE}q;d" $PACKAGE_FILE)
	reg="Exception in thread \"$THREAD\" $PACKAGE_THREAD"
	
	for((j=0; j<=$BACKTRACE_LINES; j++)); do
		FAILED_LINE=$(( ( RANDOM % 1000 )  + 1 ))
		PACKAGE_LINE=$(( ( RANDOM % 110 )  + 1 ))
		PACKAGE=$(sed "${PACKAGE_LINE}q;d" $PACKAGE_FILE)
		VERB_LINE=$(( ( RANDOM % 4554 )  + 1 ))
		VERB=$(sed "${VERB_LINE}q;d" $VERBS_FILE)
		NOUN_LINE=$(( ( RANDOM % 110 )  + 1 ))
		NOUN=$(sed "${NOUN_LINE}q;d" $NOUNS_FILE)
		CLASS_LINE=$(( ( RANDOM % 110 )  + 1 ))
		CLASS=$(sed "${CLASS_LINE}q;d" $NOUNS_FILE)
		
		reg="$reg at ${PACKAGE}.${CLASS}.${VERB}${NOUN}(${CLASS}.java)"
	done
	echo $reg >> $BACKTRACE_FILE
done