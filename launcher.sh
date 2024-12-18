#!/usr/bin/env bash

# Print JVM version and a separator line
java -version
echo "-----"

# Iterate through levels 0 to 4
for level in 0 1 2 3 4; do
    echo "JVM optimization blocked at $level"
    java -XX:TieredStopAtLevel=$level -jar dpatchperf-1.0-SNAPSHOT-jar-with-dependencies.jar
    echo "-----"
done

