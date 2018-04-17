#set JDK_HOME=c:\progra~1\j2sdk1.4.2_02\jre
set JDK_HOME=c:\progra~1\Java\jdk1.6.0_14



set JAVA_COMPCMD=FORCE_SIGNON

%JDK_HOME%\bin\java -Xss512K -Xmx1G -Djava.library.path="o:\java\csdp\semmscon" -cp o:\java\classes;o:\java\vista\lib\vista.jar;o:\java\csdpDistribution\swing\csdp\lib\jpy.jar;o:\java\csdpDistribution\swing\csdp\lib\pd.jar DWR.CSDP.Csdp

#o:\software\jdk1.1.8\bin\jre -nojit -mx64m -cp o:\software\swing1.1.1beta2\swing.jar;o:\java\classes\csdp.jar DWR.CSDP.Csdp
#o:\software\jdk1.1.8\bin\jre -mx64m -cp o:\software\swing1.1.1beta2\swing.jar;o:\java\classes\csdp.jar DWR.CSDP.Csdp
#o:\software\ibmjdk1.1.8\bin\java -classpath o:\software\ibmjdk1.1.8\lib\classes.zip;o:\software\swing1.1.1beta2\swing.jar;o:\java\classes\csdp.jar DWR.CSDP.Csdp
