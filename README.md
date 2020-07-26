## Build 

~~~
./gradlew clean build unpack
docker build --rm -t bqiaodev/deviceadmin .
~~~

## Run 

With in-memory db
~~~
docker-compose -p h2 -f docker-compose-h2.yml up -d
~~~

With mysql
~~~
docker-compose -f docker-compose-repo.yml up -d
## wait couple of seconds for db to be ready
docker-compose up -d
~~~

## Stop or remove  

~~~
docker-compose -f docker-compose-h2.yml stop
~~~

~~~
docker-compose -f docker-compose-repo.yml -f docker-compose.yml stop
## or
docker-compose -f docker-compose-repo.yml -f docker-compose.yml down
~~~


## Rebuild Docker Container after Image Change

~~~
docker-compose up -d --no-deps --build da
~~~

## Publish to local maven repository:

~~~
gradlew clean build publishToMavenLocal
~~~

## Elasticsearch

Start up Elasticsearch only
```
docker run -d --name es780 -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.8.0
```

```
http://localhost:9200/_search
http://localhost:9200/_aliases
http://localhost:9200/_aliases?pretty=true
http://localhost:9200/_cat/indices?v
```
## DB

```
docker exec -it mysql mysql -uroot -p
```
## EC2 Ubuntu

```
sudo apt install docker.io
sudo apt install docker-compose
```
Use sudo to run docker command otherwise error connecting to daemon 
`ubuntu docker Got permission denied while trying to connect to the Docker daemon socket at unix`.

```
apt-cache search docker-compose
```
