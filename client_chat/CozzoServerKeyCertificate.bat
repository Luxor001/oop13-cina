cd E:\java\jdk\jre
(echo Cozzo & echo Cozzo & echo Cozzo & echo & echo  & echo  & echo si) | keytool -genkey -alias ServerKey -keyalg RSA -keypass password -storepass password -keystore E:\java\workspace\client_chat/CozzoServerKey.jks
keytool -export -alias ServerKey -storepass password -file E:\java\workspace\client_chat/CozzoServerKeyCertificate.cer -keystore E:\java\workspace\client_chat/CozzoServerKeyKey.jks
