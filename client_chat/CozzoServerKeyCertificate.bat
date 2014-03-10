cd E:\java\jdk\jre
(echo Cozzo & echo Cozzo & echo Cozzo & echo  & echo  & echo  & echo si) | keytool -genkey -alias ServerKey -keyalg RSA -keypass Mnnt@Vh6 -storepass Mnnt@Vh6 -keystore E:\java\workspace\client_chat/CozzoServerKey.jks
keytool -export -alias ServerKey -storepass Mnnt@Vh6 -file E:\java\workspace\client_chat/CozzoServerKeyCertificate.cer -keystore E:\java\workspace\client_chat/CozzoServerKeyKey.jks
