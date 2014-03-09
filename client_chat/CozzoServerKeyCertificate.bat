cd E:\java\jdk\jre
(echo Cozzo & echo Cozzo & echo Cozzo & echo  & echo  & echo  & echo si) | keytool -genkey -alias ServerKey -keyalg RSA -keypass cQbV8lHa -storepass cQbV8lHa -keystore E:\java\workspace\client_chat/CozzoServerKey.jks
keytool -export -alias ServerKey -storepass cQbV8lHa -file E:\java\workspace\client_chat/CozzoServerKeyCertificate.cer -keystore E:\java\workspace\client_chat/CozzoServerKeyKey.jks
