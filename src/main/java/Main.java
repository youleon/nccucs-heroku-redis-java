import redis.clients.jedis.Jedis;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {

    public static void main(String[] args) throws URISyntaxException {

        port(Integer.valueOf(System.getenv("PORT")));

        URI redisURI = new URI(System.getenv("REDIS_URL"));
        Jedis client = new Jedis(redisURI);

        get("/", (req, res) -> "<h1>To use simple SET/GET/DEL on heroku-redis, try...</h1> <h2>/set/KEY/VALUE</h2> <h2>/get/KEY</h2> <h2>/getall</h2> <h2>/del/KEY</h2>");

        get("/set/:key/:value", (req, res) -> client.set(req.params(":key"), req.params(":value")));

        get("/get/:key", (req, res) -> req.params(":key") + " : " + client.get(req.params(":key")));

        get("/getall", (req, res) -> client.keys("*").stream()
                .map(s -> s + " : " + client.get(s))
                .collect(Collectors.joining(", ")));

        get("/del/:key", (req, res) -> client.del(req.params(":key")));

        get("/expire/:key/:sec", (req, res) -> client.expire(req.params(":key"), Integer.parseInt(req.params(":sec"))));

        get("/ttl/:key", (req, res) -> client.ttl(req.params(":key")));

    }

}
