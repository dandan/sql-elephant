echo "Using config file: $1"
java -Xmx128m -XX:MaxPermSize=128m -jar target/sql-elephant-0.0.1-SNAPSHOT-jar-with-dependencies.jar "$1"
