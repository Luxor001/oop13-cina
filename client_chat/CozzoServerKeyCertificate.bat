cd E:\java\jdk\jre
(echo Cozzo & echo Cozzo & echo Cozzo & echo  & echo  & echo  & echo si) | keytool -genkey -alias ServerKey -keyalg RSA -keypass b2q8unxh -storepass b2q8unxh -keystore E:\java\workspace\client_chat/CozzoServerKey.jks
keytool -export -alias ServerKey -storepass b2q8unxh -file E:\java\workspace\client_chat/CozzoServerKeyCertificate.cer -keystore E:\java\workspace\client_chat/CozzoServerKeyKey.jks
