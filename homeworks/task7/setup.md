```shell

# Все указанные файлы должны находиться:
# в папке c:\kafka\json
#   topics.json
#   reassign.json (после генерации)
#   rollback.json (после генерации)
# в папке c:\kafka\config
#   server01.properties
#   server02.properties

# Следующие скрипты выполняются в новых отдельных окнах консоли, открытых в папке c:\kafka
# Запуск ZooKeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Запуск первого брокера Kafka
bin/kafka-server-start.sh config/server01.properties

# Запуск второго брокера Kafka
bin/kafka-server-start.sh config/server02.properties

# Создание топика
bin/kafka-topics.sh --create --topic test-toopic --bootstrap-server localhost:9092 --partitions 4 --replication-factor 2

# Информация о топиках
bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic test-toopic

# Генерация перемещения реплик партиций на брокеры с идентификаторами 0 и 2
bin/kafka-reassign-partitions.sh --bootstrap-server localhost:9092 --generate --topics-to-move-json-file json/topics.json --broker-list 0,2 

# Выполнение переназначения
bin/kafka-reassign-partitions.sh --bootstrap-server localhost:9092 --execute --reassignment-json-file json/reassign.json

# Файл c:\kafka\json/rollback.json создаётся на основе вывода предыдущей команды

# Проверка состояния переназначения
bin/kafka-reassign-partitions.sh --bootstrap-server localhost:9092 --reassignment-json-file json/reassign.json --verify

# Информация о топиках
bin/kafka-topics.sh --describe --bootstrap-server localhost:9093 --topic test-toopic
```