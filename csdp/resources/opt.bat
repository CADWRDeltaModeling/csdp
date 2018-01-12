#set JDK_HOME=c:\progra~1\j2sdk1.4.2_04
#set JDK_HOME=c:\progra~1\jdk1.31_05
#set JDK_HOME=c:\progra~1\j2sdk1.4.1_05
set JDK_HOME=c:\progra~1\Java\jdk1.6.0_14

cd dialog

%JDK_HOME%\bin\javac -O -g -deprecation -classpath %JDK_HOME%\lib\classes.zip;c:\vista\lib\vista.jar;c:\vista\lib\pd.jar;o:\java\csdpDistribution\swing\csdp\lib\jpy.jar *.java
copy *.class o:\java\classes\DWR\CSDP\dialog

cd ..
cd semmscon
%JDK_HOME%\bin\javac -O DWR\CSDP\semmscon\UseSemmscon.java
copy DWR\CSDP\semmscon\UseSemmscon.class o:\java\classes\DWR\CSDP\semmscon
copy *.class o:\java\classes\DWR\CSDP\semmscon

cd ..
%JDK_HOME%\bin\javah -classpath o:\java\classes -jni DWR.CSDP.semmscon.UseSemmscon
copy DWR_CSDP_semmscon_UseSemmscon.h semmscon
cd semmscon
bcc32 -WD -RT- -O2 -x- -v -Io:\java\csdp\semmscon DWR_CSDP_semmscon_utmconvert semmscon.lib

cd ..
%JDK_HOME%\bin\javac -O -g -deprecation -classpath c:\progra~1\jdk1.31_05\lib\classes.zip;c:\vista\lib\vista.jar;c:\vista\lib\pd.jar;o:\java\csdpDistribution\swing\csdp\lib\jpy.jar;o:\java\classes\ *.java
copy *.class o:\java\classes\DWR\CSDP

cd images
copy *.gif o:\java\classes\DWR\CSDP\images
copy *.jpg o:\java\classes\DWR\CSDP\images

cd o:\java\classes
%JDK_HOME%\bin\jar -cvf csdp.jar COM DWR/CSDP
cd o:\java\csdp

