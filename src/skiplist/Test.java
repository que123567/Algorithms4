package skiplist;

public class Test
{

    public static void main(String[] args) {
        SkipList<String> s = new SkipList<String>();
        s.put("ABC", "");
        s.put("DEF", "");
        s.put("KLM", "");
        s.put("HIJ", "");
        s.put("GHJ", "");
        s.put("AAA", "");

        s.remove("ABC");
        s.remove("DEF");
        s.remove("KLM");
        s.remove("HIJ");
        s.remove("GHJ");
        s.remove("AAA");


        s.put("ABC", "");
        s.put("DEF", "");
        s.put("KLM", "");
        s.put("HIJ", "");
        s.put("GHJ", "");
        s.put("AAA", "");
//        s.printHorizontal();

    }

}