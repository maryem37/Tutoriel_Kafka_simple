# Tutoriel_Kafka_simple
Tutoriel complet pour débutants sur Apache Kafka incluant l'installation, la configuration en mode KRaft, la création de topics, l'utilisation des producers et consumers, ainsi que les commandes essentielles pour la gestion de Kafka.

---

## 1. Activer Kafka

    C:\kafka1\kafka1\bin\windows>kafka-storage.bat random-uuid
    Exx9yhoRReeG4XgdWaUemQ
    
    C:\kafka1\kafka1\bin\windows>kafka-storage.bat format -t Exx9yhoRReeG4XgdWaUemQ -c C:/kafka1/kafka1/config/kraft/server.properties
    Formatting metadata directory C:/kafka1/logs with metadata.version 3.9-IV0.
    
    
    C:\kafka1\kafka1\bin\windows>kafka-server-start.bat C:/kafka1/kafka1/config/kraft/server.properties

ouvrir un nouveaux terminal
---
2.Créer un topic
---

    cd C:\kafka1\kafka1\bin\windows
    
    kafka-topics.bat --create --topic commandes --bootstrap-server localhost:9092 --partitions 2 --replication-factor 1

rem Vérifier

    kafka-topics.bat --list --bootstrap-server localhost:9092

rem Voir les détails

    kafka-topics.bat --describe --topic commandes --bootstrap-server localhost:9092

---
3.Terminal 3 — Consumer (bloqué en attente) ouvrir un3 eme terminal
---
    cd C:\kafka1\kafka1\bin\windows
    
    kafka-console-consumer.bat --topic commandes --bootstrap-server localhost:9092 --group groupe-A --from-beginning --property print.key=true --property print.offset=true --property key.separator=" | "

 ---
 
4.Terminal 2 — Lancer le Producer
---
    kafka-console-producer.bat --topic commandes --bootstrap-server localhost:9092 --property "parse.key=true" --property "key.separator=:"
Tape tes messages :

    >client-1:commande iPhone
    >client-2:commande Samsung
    >client-1:commande AirPods
    >client-3:commande Laptop
    >client-2:commande Chargeur
    >client-1:commande Coque

==> Regarde Terminal 3 — chaque message apparaît avec sa clé et son offset.
Quand tu as fini : Ctrl+C pour quitter le producer.

---

Terminal 2 — Inspecter après envoi
---
Maintenant que le producer est arrêté, le Terminal 2 est libre. Lance ces commandes :
rem Lister les groupes

    kafka-consumer-groups.bat --bootstrap-server localhost:9092 --list
rem Voir les offsets et le lag du groupe-A

    kafka-consumer-groups.bat --bootstrap-server localhost:9092 --group groupe-A --describe

 ---
Terminal 3 — Tester le lag
---
Arrête le consumer avec Ctrl+C, puis dans Terminal 2 relance le producer :

    kafka-console-producer.bat --topic commandes --bootstrap-server localhost:9092

    >message-pendant-absence-1
    >message-pendant-absence-2
    >message-pendant-absence-3
Ctrl+C pour quitter le producer, puis inspecte le lag :

    kafka-consumer-groups.bat --bootstrap-server localhost:9092 --group groupe-A --describe

<img width="1222" height="172" alt="image" src="https://github.com/user-attachments/assets/b3d44566-58d2-45f4-bba8-979261263c6c" />

---
Terminal 3 — Rattraper le lag (replay)

---
Relance le consumer dans Terminal 3 :

    kafka-console-consumer.bat --topic commandes --bootstrap-server localhost:9092 --group groupe-A --from-beginning --property print.key=true --property print.offset=true --property key.separator=" | "

<img width="1280" height="256" alt="image" src="https://github.com/user-attachments/assets/60b903cf-98c9-4b3f-b547-41f9ac592884" />
Il relit tout depuis le début et rattrape les messages manqués.
#Terminal 2 — Reset offset complet
Arrête le consumer Terminal 3 avec Ctrl+C, puis :

    kafka-consumer-groups.bat --bootstrap-server localhost:9092 --group groupe-A --topic commandes --reset-offsets --to-earliest --execute

 ---
 
Ce qu'on a prouvé concrètement

---

##Quand tu as tapé client-1:commande iPhone dans le producer et que ça est apparu dans le consumer, tu as prouvé que :

le producer ne connaît pas l'adresse du consumer
le consumer ne connaît pas l'adresse du producer
les deux parlent uniquement avec localhost:9092
le broker est le seul intermédiaire

---

Preuve 2 — Le découplage temporel (le plus important)
---
En faisant Ctrl+C sur le consumer puis en envoyant 3 messages, tu as prouvé que :

le producer n'a pas planté quand le consumer était absent
les messages sont partis quand même vers le broker
le broker les a stockés sur disque sans attendre personne

C'est la différence fondamentale avec un appel HTTP — si le serveur est down, HTTP échoue. Kafka, lui, garde les messages.
---

La grande différence avec les autres systèmes
---
SystèmeQue se passe-t-il si le consommateur est absent ?

        | Système   | Que se passe-t-il si le consommateur est absent ? |
        | --------- | ------------------------------------------------- |
        | HTTP/REST | La requête échoue, le message est perdu           |
        | RabbitMQ  | Message en queue, supprimé après lecture          |
        | Kafka     | Message persisté sur disque, relu quand on veut   |



https://github.com/user-attachments/assets/181be91b-a7b1-42e9-89ce-6b3bf7f8e27a




