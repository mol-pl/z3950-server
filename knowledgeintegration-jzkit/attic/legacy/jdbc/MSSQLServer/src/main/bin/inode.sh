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
[ -z "$INODE_HOME" ] && INODE_HOME=`cd "$PRGDIR/.." ; pwd`

if [ -z "$INODE_BASE" ] ; then
  INODE_BASE="$INODE_HOME"
fi

echo Using INODE_HOME=$INODE_HOME
echo Using INODE_BASE=$INODE_BASE

if [ "$1" = "start" ] ; then

  touch $INODE_BASE/logs/inode.out

  CLASSPATH=$INODE_BASE/etc

  JAVA_OPTS="-Xmx512m -Xms256m -Djava.util.logging.config.file=./logging.properties -Dcom.k_int.inode.tmpdir=$INODE_HOME/tmp"
  for jar in `ls $INODE_HOME/lib/*.jar`
  do
    CLASSPATH=$CLASSPATH:$jar
  done

  export CLASSPATH

  echo Starting iNode service with INODE_HOME = $INODE_HOME
  java -classpath $CLASSPATH $JAVA_OPTS com.k_int.ia.service.IAServiceManager DefaultApplicationContext.xml INodeServiceAppContext.xml >> $INODE_BASE/logs/inode.out 2>&1 &
elif [ "$1" = "stop" ] ; then
  echo Stop 
else
  echo Unknown command
fi
