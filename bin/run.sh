echo "Using config file: $1"

MAVEN_OPTS=-Xmx128m mvn exec:java -Dexec.mainClass="org.laeng.app.sql_elephant.Main" -Dexec.args="$1"
