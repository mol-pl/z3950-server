echo Setting classpath...
for jar in `ls ~/.maven/repository/*/jars/*.jar ../target/jzkit2_service-2.0.0.jar`
do
  export CLASSPATH=$CLASSPATH:$jar
done
export CLASSPATH=../etc:../classes/config:$CLASSPATH

java org.jzkit.service.JZKitService ./JZKit2.properties
