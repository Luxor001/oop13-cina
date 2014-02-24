@echo off
cd E:\java\jdk\jre
(echo francesco cozzolino & echo cozzo & echo cozzo & echo misano adriatico & echo rn & echo it & echo si) | keytool -genkey -alias ClientKey -keyalg RSA -keypass changeit -storepass changeit -keystore E:\java\workspace\client_chat/FrancescoClientKey.jks
keytool -export -alias ClientKey -storepass changeit -file E:\java\workspace\client_chat/FrancescoClientCertificate.cer -keystore E:\java\workspace\client_chat/FrancescoClientKey.jks
echo on
