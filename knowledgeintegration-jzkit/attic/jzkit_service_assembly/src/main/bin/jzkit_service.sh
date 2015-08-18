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

if [ "$1" = "start" ] ; then

  mkdir -p $JZKIT_BASE/logs
  touch $JZKIT_BASE/logs/jzkit.out

  CLASSPATH=$JZKIT_BASE/etc

  JAVA_OPTS="-Xmx512m -Xms256m -Djava.util.logging.config.file=./logging.properties -Dcom.k_int.jzkit.tmpdir=$JZKIT_HOME/tmp"
  for jar in `ls $JZKIT_HOME/lib/*.jar`
  do
    CLASSPATH=$CLASSPATH:$jar
  done

  export CLASSPATH

  echo Starting iNode service with JZKIT_HOME = $JZKIT_HOME
  cd $JZKIT_BASE/bin
  java -classpath $CLASSPATH $JAVA_OPTS org.jzkit.service.IAServiceManager DefaultApplicationContext.xml >> $JZKIT_BASE/logs/jzkit.out 2>&1 &
  echo $! > ../etc/jzkit.pid
elif [ "$1" = "stop" ] ; then
  echo Stop 
  kill -9 `cat ../etc/jzkit.pid`
  rm ../etc/jzkit.pid
else
  echo Unknown command
fi
