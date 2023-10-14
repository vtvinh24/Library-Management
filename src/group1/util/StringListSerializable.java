package group1.util;

import java.util.List;

public interface StringListSerializable
{
    List<String> serialize();
    void deserialize(List<String> input);
}
