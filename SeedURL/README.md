# Solution de crawling

## Liste des composants

- Frontend : ReactJS

Doit pouvoir récupérer l'URL
Télécharger le fichier de crawl

- SeedURL : NodeJS

Récupère l'URL pour la stocker en base de données.
Envoie l'URL au sein de Kafka

- CrawlURL : Java

Crawl l'URL et stocke les données en base de données
Stocke les données dans un répertoire sur le bureau

## Contrainte :

- Une URL ne peut être traité deux fois par le système (Redis)
- Deux réplicas minimum pour le Crawling

- Kubernetes / Docker / Grafana / Prometheus / ELK

## Compréhension de Kafka

Si plusieurs consommateurs appartiennent au même groupe, le message est lu une seule fois par l’un des consommateurs de ce groupe.
Si plusieurs groupes de consommateurs lisent le même topic, alors le message sera lu une fois par chaque groupe.

Les topics sont divisés en partitions, chacune possédant un offset permettant de se situer dans la partition et d’éviter de lire plusieurs fois le même message.
Si le nombre de consommateurs dépasse le nombre de partitions d’un topic, les consommateurs excédentaires resteront inactifs.

## Lancement de Kafka

- cd /Users/dylanpion/Desktop/kafka_2.13-3.9.0;
- bin/zookeeper-server-start.sh config/zookeeper.properties

Après dans un autre terminal entré :

- bin/kafka-server-start.sh config/server.properties

## Lancement de mongodb :

brew services start mongodb-community@8.0

Voir les Messages dans le topic :
cd /Users/dylanpion/Desktop/kafka_2.13-3.9.0
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic crawlers-url --from-beginning

Test manuel d'envoie de message :
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic crawlers-url --from-beginning
Ecrire le message

## Lancement de redis : redis-server
