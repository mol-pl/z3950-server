#!
rm -Rf output
mvn clean assembly:assembly
cd output/directory
unzip JZKitService-bin.zip
cd JZKitService/bin
./jzkit_service.sh start
