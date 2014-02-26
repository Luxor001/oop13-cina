cd E:\java\jdk\jre
(echo Cozzo & echo Cozzo & echo Cozzo & echo & echo  & echo  & echo si) | keytool -genkey -alias ClientKey -keyalg RSA -keypass changeit -storepass changeit -keystore E:\java\workspace\client_chat/CozzoClientKey.jks
keytool -export -alias ClientKey -storepass changeit -file E:\java\workspace\client_chat/CozzoClientKeyCertificate.cer -keystore E:\java\workspace\client_chat/CozzoClientKeyKey.jks
