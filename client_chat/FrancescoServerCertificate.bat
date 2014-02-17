@echo off
cd E:\java\jdk\jre
(echo francesco cozzolino & echo cozzo & echo cozzo & echo misano adriatico & echo rn & echo it & echo si) | keytool -genkey -alias ServerKey -keyalg RSA -keypass password -storepass password -keystore E:\java\workspace\client_chat\FrancescoServerKey.jks
keytool -export -alias ServerKey -storepass password -file E:\java\workspace\client_chat\FrancescoServerCertificate.cer -keystore E:\java\workspace\client_chat\FrancescoServerKey.jks
echo on
