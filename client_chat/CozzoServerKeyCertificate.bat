cd E:\java\jdk\jre
(echo cozzo & echo cozzo & echo cozzo & echo & echo  & echo  & echo si) | keytool -genkey -alias ServerKey -keyalg RSA -keypass password -storepass password -keystore E:\java\workspace\client_chat/cozzoServerKey.jks
keytool -export -alias ServerKey -storepass password -file E:\java\workspace\client_chat/cozzoServerKeyCertificate.cer -keystore E:\java\workspace\client_chat/cozzoServerKeyKey.jks
