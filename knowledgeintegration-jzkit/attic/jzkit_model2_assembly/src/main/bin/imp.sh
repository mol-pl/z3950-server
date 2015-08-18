CP=../etc
for jar in `ls ../lib/*.jar`
do
  CP=$CP:$jar
done

java -cp $CP com.k_int.ispp.importers.ExtractFileImporter $*
