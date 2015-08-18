echo Setting classpath...
for jar in `ls ~/.maven/repository/*/jars/*.jar ../target/jzkit2_service-2.0.0.jar`
do
  export CLASSPATH=$CLASSPATH:$jar
done
export CLASSPATH=../etc:../classes/config:$CLASSPATH

mysql -u root --password=pa55word <<!!!
drop database if exists JZKit2;
create database JZKit2;
grant all on JZKit2.* to 'k-int'@localhost identified by 'k-int';
grant all on JZKit2.* to 'k-int'@localhost.localdomain identified by 'k-int';
grant all on JZKit2.* to 'k-int'@'%' identified by 'k-int';
FLUSH PRIVILEGES
!!!

echo Calling...
java org.jzkit.install.JZKitDBInstaller ../etc/DefaultApplicationContext.xml
