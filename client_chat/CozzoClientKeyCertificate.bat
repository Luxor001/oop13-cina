cd E:\java\jdk\jre
(echo Cozzo & echo Cozzo & echo Cozzo & echo  & echo  & echo  & echo si) | keytool -genkey -alias ClientKey -keyalg RSA -keypass 6MKbmwy0 -storepass 6MKbmwy0 -keystore E:\java\workspace\client_chat/CozzoClientKey.jks
keytool -export -alias ClientKey -storepass 6MKbmwy0 -file E:\java\workspace\client_chat/CozzoClientKeyCertificate.cer -keystore E:\java\workspace\client_chat/CozzoClientKeyKey.jks
