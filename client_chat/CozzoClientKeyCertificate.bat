cd E:\java\jdk\jre
(echo Cozzo & echo Cozzo & echo Cozzo & echo  & echo  & echo  & echo si) | keytool -genkey -alias ClientKey -keyalg RSA -keypass naamRp7V -storepass naamRp7V -keystore E:\java\workspace\client_chat/CozzoClientKey.jks
keytool -export -alias ClientKey -storepass naamRp7V -file E:\java\workspace\client_chat/CozzoClientKeyCertificate.cer -keystore E:\java\workspace\client_chat/CozzoClientKeyKey.jks
