CLASSPATH=../etc

for jar in `ls ../lib/*.jar`
do
  export CLASSPATH=$CLASSPATH:$jar
done

java $OPTS com.k_int.openrequest.setup.Setup ./BaseApplicationContext.xml
