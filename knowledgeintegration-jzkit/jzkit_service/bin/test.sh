echo Setting classpath...
for jar in `ls ../../lib/*.jar ../../dist/*.jar`
do
  export CLASSPATH=$CLASSPATH:$jar
done
export CLASSPATH=$CLASSPATH:../config:../test/classes:../../etc

echo Calling...
java org.jzkit.service.test.TestService
