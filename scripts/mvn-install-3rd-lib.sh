mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
                         -Dfile=/home/mina/Desktop/wsq1000/java/aware-wsq1000-2.1.1.1.jar  \
                         -DgroupId=com.aware.WSQ1000 \
                         -DartifactId=wsq1000 \
                         -Dversion=2.1.1.1 \
                         -Dpackaging=jar -DlocalRepositoryPath=../libs