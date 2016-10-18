JAVA_OPTS="-server -Xmx2g -Djna.nosys=true -XX:+HeapDumpOnOutOfMemoryError -Djava.awt.headless=true -Dfile.encoding=UTF8 -Drhino.opt.level=9"
LOG_SETTINGS="-Dlog4j.configuration="file:src/main/resources/logging/log4j.properties""



java -cp log4j-core-2.7.jar "org.apache.logging.log4j.core.tools.Generate$CustomLogger"
       org/fao/ess/cstat/migration/utils/log/ExternalLog
       DEFCON1=350 DEFCON2=450 DEFCON3=550 >/home/faber-cst/Projects/cstat-migration/src/main/java/org/fao/ess/cstat/migration/utils/log/ExternalLog.java
