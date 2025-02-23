# myna

Virtual board games.

<table>
  <tr>
    <td>
      <img src="docs/myna.jpg" alt="common myna" />
    </td>
    <td>The <em><a href="https://en.wikipedia.org/wiki/Common_myna">common myna</a></em> is a bird in the <a href="https://en.wikipedia.org/wiki/Sturnidae">family Sturnidae</a>, native to Asia. An omnivorous open woodland bird with a strong territorial instinct, the common myna has adapted extremely well to urban environments.<br/><br/><br/><br/><br/><br/><br/><sub><sup>Photo <a href="https://commons.wikimedia.org/w/index.php?curid=66394278">by Gerrie van Vuuren, CC BY-SA 4.0</a></sup></sub></td>
  </tr>
</table>


## What is it?

`myna` is a web app for virtual board games. The main idea is to provide a virtual table for any board game. 
It doesn't force players to follow the rules of a game. Instead, like in real life, there are different game 
objects to interact with.

Currently only simple movable game objects are supported, but there are plans to support more:

| Object type | New ability |
| ----------- | ----------- |
| Object with multiple states that change in a particular or random order | Dice, power-ups, active player indicator |
| Object stacks, pick game objects from the stack in a particular or random order | Card deck, object placement games |
| Additional game zones hidden from other players | Virtual hand for card games |

## Demo


https://github.com/maizy/myna/assets/789623/97952daa-3406-48a6-af20-c89ebdedb17b

## Run with docker-compose

* setup docker-compose.yml based on [deploy/docker-compose/docker-compose.yml](deploy/docker-compose/docker-compose.yml)
* setup myna.env based on [deploy/docker-compose/myna.env.example](deploy/docker-compose/myna.env.example)
* run `docker compose up -d`
* if you need a reverse proxy, see the sample nginx config in [deploy/docker-compose/myna-nginx.conf](deploy/docker-compose/myna-nginx.conf)

## Run with k8s

* setup `deploy/helm/myna/values.yaml`
* deploy

```shell
cd deploy/helm
helm dependency build myna
helm install <NAME> myna
```

## Technical details

I made it because I needed to revise Spring ecosystem and do something useful at the same time.

So, the key points of this project are

* Based on Spring components: Spring Boot 3.x, Spring Web MVC, Spring Security, Spring Data
* Service is stateless, it's ready to run in a distributed manner
* All state stored in Redis and PostgresSQL
* Redis pubsub is used for the internal message bus, and all messages pass through this bus
* Websocket is used for communication between backend and frontend parts

## Developer environment

For IntelliJ Idea:
* don't forget to enable annotation processing

Requirements:
* JDK 21
* docker
* docker compose

Start DB:

```shell
./dev/start-db.sh
```

Start project in dev mode:

```shell
./gradlew bootRun
```

Or use run configurations for IntelliJ IDEA.

## Build & publish

```shell
./gradlew bootBuildImage
docker push ghcr.io/maizy/myna:x.y.z

docker tag ghcr.io/maizy/myna:x.y.z ghcr.io/maizy/myna:latest
docker push ghcr.io/maizy/myna:latest
```
