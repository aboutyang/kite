import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author yangwang【yangwang@jd.com】
 * @date 2018/8/17 12:40
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true)
public class Zoo {

    @JsonSubTypes({
            @JsonSubTypes.Type(value = Zoo.Dog.class, name = "dog"),
            @JsonSubTypes.Type(value = Zoo.Cat.class, name = "cat")
    })
    public Animal animal;

//    public String type;

    public static class Animal {
        public String name;
    }

    @JsonTypeName("dog")
    public static class Dog extends Animal {
        public double barkVolume;
    }

    @JsonTypeName("cat")
    public static class Cat extends Animal {
        boolean likesCream;
        public int lives;
    }

}
