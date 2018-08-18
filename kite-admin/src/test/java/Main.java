import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author yangwang【yangwang@jd.com】
 * @date 2018/8/17 12:40
 */
public class Main {

    public static void main(String[] args) throws IOException {

        Zoo.Dog dog = new Zoo.Dog();
        dog.name = "Spike";
        dog.barkVolume = 1.1;
        Zoo zoo = new Zoo();
        zoo.animal = dog;

        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(zoo);
        System.out.println(result);
//
//        String dogStr = "{\"animal\":{\"name\":\"Spike\",\"barkVolume\":1.1},\"type\":\"dog\"}";
//        String catStr = "{\"animal\":{\"name\":\"Spike\",\"barkVolume\":1.1},\"type\":\"cat\"}";
//
//        Zoo zoo1 = mapper.readValue(dogStr, Zoo.class);
//
//        System.out.println(zoo1.animal.name);
//        System.out.println(zoo1.animal.getClass());
//
//        Zoo zoo2 = mapper.readValue(catStr, Zoo.class);
//        System.out.println(zoo2.animal.name);
//        System.out.println(zoo2.animal.getClass());
    }
}
