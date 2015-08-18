CLASSPATH=../etc

for jar in `ls ../lib/*.jar`
do
  export CLASSPATH=$CLASSPATH:$jar
done

DEFAULT_DATABASE_NAME=OpenRequest
DEFAULT_DATABASE_ADMIN_USER=root
DEFAULT_DATABASE_ADMIN_PASS=password

read -p "Database Name [$DEFAULT_DATABASE_NAME] : " DATABASE_NAME
read -p "Database Name [$DEFAULT_DATABASE_ADMIN_USER] : " DATABASE_ADMIN_USER
read -p "Database Name [$DEFAULT_DATABASE_ADMIN_PASS] : " DATABASE_ADMIN_PASS

if [ -z $DATABASE_NAME ]
then
  DATABASE_NAME=$DEFAULT_DATABASE_NAME
fi

if [ -z $DATABASE_ADMIN_USER ]
then
  DATABASE_ADMIN_USER=$DEFAULT_DATABASE_ADMIN_USER
fi

if [ -z $DATABASE_ADMIN_PASS ]
then
  DATABASE_ADMIN_PASS=$DEFAULT_DATABASE_ADMIN_PASS
fi

if ( [ -f ../etc/DefaultContext.properties ] && [ -f ./run.sh ] )
then

echo About to drop database $DATABASE_NAME

sleep 5

echo Dropping Database $DATABASE_NAME

mysql -u $DATABASE_ADMIN_USER -h localhost --password=$DATABASE_ADMIN_PASS <<!!!
drop database if exists $DATABASE_NAME;
create database $DATABASE_NAME DEFAULT CHARACTER SET utf8;
grant all on $DATABASE_NAME.* to 'k-int'@localhost identified by 'k-int';
grant all on $DATABASE_NAME.* to 'k-int'@localhost.localdomain identified by 'k-int';
grant all on $DATABASE_NAME.* to 'k-int'@'%' identified by 'k-int';
FLUSH PRIVILEGES;
!!!





java $OPTS com.k_int.openrequest.setup.Setup ./BaseApplicationContext.xml

mysql -u $DATABASE_ADMIN_USER -h localhost --password=$DATABASE_ADMIN_PASS <<!!!
use $DATABASE_NAME
create view TC_ROLES as select AUTH.USERNAME as USERNAME, P2.NAME as ROLE from
AI_AUTH_DETAILS AUTH, IA_PARTY P2, USER_ROLE UR where AUTH.USER_ID=UR.USERNAME
and P2.ID=UR.ROLE;
!!!

else
echo ../etc/DefaultContext.properties is not set or not run from bin directory
fi


