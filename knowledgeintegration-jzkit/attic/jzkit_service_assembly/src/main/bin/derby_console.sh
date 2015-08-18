# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set CATALINA_HOME if not already set
[ -z "$JZKIT_HOME" ] && JZKIT_HOME=`cd "$PRGDIR/.." ; pwd`

if [ -z "$JZKIT_BASE" ] ; then
  JZKIT_BASE="$JZKIT_HOME"
fi

echo Using JZKIT_HOME=$JZKIT_HOME
echo Using JZKIT_BASE=$JZKIT_BASE

CLASSPATH=$JZKIT_BASE/etc

JAVA_OPTS="-Xmx512m -Xms256m -Djava.util.logging.config.file=./logging.properties -Dcom.k_int.jzkit.tmpdir=$JZKIT_HOME/tmp"
for jar in `ls $JZKIT_HOME/lib/*.jar`
do
  CLASSPATH=$CLASSPATH:$jar
done

echo Starting derby console...
echo usually use: 
echo connect 'jdbc:derby:jzkit';
java -classpath $CLASSPATH $JAVA_OPTS org.apache.derby.tools.ij
