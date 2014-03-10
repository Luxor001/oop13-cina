cd E:\java\jdk\jre
(echo Cozzo & echo Cozzo & echo Cozzo & echo  & echo  & echo  & echo si) | keytool -genkey -alias ClientKey -keyalg RSA -keypass 1HVzRT3C -storepass 1HVzRT3C -keystore E:\java\workspace\client_chat/CozzoClientKey.jks
keytool -export -alias ClientKey -storepass 1HVzRT3C -file E:\java\workspace\client_chat/CozzoClientKeyCertificate.cer -keystore E:\java\workspace\client_chat/CozzoClientKeyKey.jks
