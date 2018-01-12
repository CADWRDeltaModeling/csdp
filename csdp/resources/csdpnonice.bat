#set JDK_HOME=c:\progra~1\j2sdk1.4.2_02\jre
set JDK_HOME=c:\progra~1\Java\jdk1.5.0_01



set JAVA_COMPCMD=FORCE_SIGNON

%JDK_HOME%\bin\java -Xmx256m -Djava.library.path="d:\java\csdp\semmscon" -cp d:\java\classes;d:\java\vista\lib\vista.jar;d:\java\csdpDistribution\swing\csdp\lib\jpy.jar;d:\java\csdpDistribution\swing\csdp\lib\pd.jar DWR.CSDP.Csdp

#d:\software\jdk1.1.8\bin\jre -nojit -mx64m -cp d:\software\swing1.1.1beta2\swing.jar;d:\java\classes\csdp.jar DWR.CSDP.Csdp
#d:\software\jdk1.1.8\bin\jre -mx64m -cp d:\software\swing1.1.1beta2\swing.jar;d:\java\classes\csdp.jar DWR.CSDP.Csdp
#d:\software\ibmjdk1.1.8\bin\java -classpath d:\software\ibmjdk1.1.8\lib\classes.zip;d:\software\swing1.1.1beta2\swing.jar;d:\java\classes\csdp.jar DWR.CSDP.Csdp
