find -name "*.java" > sources.txt
javac -source 1.5 -d bin @sources.txt $*
echo Finished.

