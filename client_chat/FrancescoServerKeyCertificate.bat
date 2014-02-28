cd E:\java\jdk\jre
(echo Francesco & echo Francesco & echo Francesco & echo & echo  & echo  & echo si) | keytool -genkey -alias ServerKey -keyalg RSA -keypass password -storepass password -keystore E:\java\workspace\client_chat/FrancescoServerKey.jks
keytool -export -alias ServerKey -storepass password -file E:\java\workspace\client_chat/FrancescoServerKeyCertificate.cer -keystore E:\java\workspace\client_chat/FrancescoServerKeyKey.jks
