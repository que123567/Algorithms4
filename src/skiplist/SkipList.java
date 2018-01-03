package skiplist;

import java.util.Random;

public class SkipList<V>
{
    public SkipListEntry<V> head; // 顶层的第一个元素
    public SkipListEntry<V> tail; // 顶层的最后一个元素

    public int size; // 跳跃表中的元素个数

    public int height; // 跳跃表的高
    public Random flag; // 投掷硬币

    /**
     * 默认构造函数
     *
     * @author xxx 2017年2月14日 下午9:32:22
     * @since v1.0
     */
    public SkipList() {
        head = new SkipListEntry<V>(SkipListEntry.negInf, null);
        tail = new SkipListEntry<V>(SkipListEntry.posInf, null);

        head.right = tail;
        tail.left = head;

        size = 0;
        height = 0;
        flag = new Random();
    }

    /**
     * 返回元素的个数
     *
     * @return
     * @author xxx 2017年2月14日 下午9:35:22
     * @since v1.0
     */
    public int size() {
        return size;
    }

    /**
     * 判断跳表中的元素个数是否为零
     *
     * @return
     * @author xxx 2017年2月14日 下午9:35:52
     * @since v1.0
     */
    public boolean isEmpty() {
        return (size == 0);
    }


    /**
     * 从最顶层的第一个元素，也即head元素开始查找，直到找到第0层、要插入的位置前面的那个key
     *
     * @param k
     * @return
     * @author xxx 2017年2月14日 下午9:42:12
     * @since v1.0
     */
    private SkipListEntry<V> findEntry(String k) {
        SkipListEntry<V> p = head;

        while (true) {
            /*
             * 一直向右找，例: k=34。 10 ---> 20 ---> 30 ---> 40 ^ | p 会在30处停止
             */
            while (p.right.key != SkipListEntry.posInf && p.right.key.compareTo(k) <= 0) {
                p = p.right;
            }
            // 如果还有下一层，就到下一层继续查找
            if (p.down != null) {
                p = p.down;
            } else {
                break; // 到了最下面一层 就停止查找
            }
        }

        return p; // p.key <= k
    }

    /**
     * 返回和key绑定的值
     */
    public V get(String k) {
        SkipListEntry<V> p = findEntry(k);

        if (k.equals(p.getKey())) {
            return p.value;
        } else {
            return null;
        }
    }


    /**
     * 往跳表中插入一个键值对，如果键已经存在，则覆盖相应的值并返回旧值
     *
     * @param k
     * @param v
     * @return
     * @author xxx 2017年2月14日 下午9:48:54
     * @since v1.0
     */
    public V put(String k, V v) {
//        System.out.println("-----插入[" + k + "]之前的跳跃表是:-----");
//        printHorizontal();

        SkipListEntry<V> p, q;

        p = findEntry(k);

        if (k.equals(p.getKey())) {
            V old = p.value;
            p.value = v;
            return old;
        }

        q = new SkipListEntry<V>(k, v);
        q.left = p;
        q.right = p.right;
        p.right.left = q;
        p.right = q;

        int currentLevel = 0; // 当前层 currentLevel = 0

        // 随机值小于0.5，则插入的键值对对应的键需要在上一层建立关联，同时有可能增加跳表的高度
        while (flag.nextDouble() < 0.5) {
            // 如果超出了高度，需要重新建一个顶层
            if (currentLevel >= height) {
                SkipListEntry<V> p1, p2;

                height = height + 1;
                p1 = new SkipListEntry<V>(SkipListEntry.negInf, null);
                p2 = new SkipListEntry<V>(SkipListEntry.posInf, null);

                p1.right = p2;
                p1.down = head;

                p2.left = p1;
                p2.down = tail;

                head.up = p1;
                tail.up = p2;

                head = p1;
                tail = p2;
            }

            while (p.up == null) {
                p = p.left;
            }
            p = p.up;

            SkipListEntry<V> e;
 
            /*
             * 注意，本实现中只有第0层的链表持有键对应的值，1 ~ height 层中的SkipListEntry对象
             * 仅仅持有键的引用，值为空，以便节省空间。
             */
            e = new SkipListEntry<V>(k, null);
            e.left = p;
            e.right = p.right;
            e.down = q;
            p.right.left = e;
            p.right = e;
            q.up = e;

            q = e; // q 进行下一层迭代
            currentLevel = currentLevel + 1; // 当前层 +1

        }
        // 插入一个键值对后总数加1
        size = size + 1;

        System.out.println("-----插入[" + k + "]之后的跳跃表是:-----");
        printHorizontal();
        return null;
    }

    /**
     * 根据键删除键值对
     *
     * @param key
     * @return
     * @author xxx 2017年2月14日 下午10:08:17
     * @since v1.0
     */
    public void remove(String key) {
        SkipListEntry<V> p = findEntry(key);

        if (!p.getKey().equals(key)) {
            return;
        }

        //删除元素后重新关联，同时使被删除的对象游离，便于垃圾回收
        p.left.right = p.right;
        p.right.left = p.left;
        p.right = null;
        p.left = null;
        //自底向上，使所有键等于key的SkipListEntry对象左右两个方向的引用置空
        while (p.up != null) {
            p = p.up;
            p.left.right = p.right;
            p.right.left = p.left;
            p.right = null;
            p.left = null;
        }

        //自顶向下，使所有键等于key的SkipListEntry对象上下两个方向的引用置空
        while (p.down != null) {
            SkipListEntry<V> temp = p.down;
            p.down = null;
            temp.up = null;
            p = temp;
        }
 
        /*
         * 删除元素后，如果顶层的链表只有head和tail两个元素，则删除顶层。
         * 删除顶层以后最新的顶层如果依然只有head和tail两个元素，则也要被删除，以此类推。
         */
        while (head.right.key == tail.key && height > 0) {
            SkipListEntry<V> p1, p2;
            p1 = head.down;
            p2 = tail.down;

            head.right = null;
            head.down = null;

            tail.left = null;
            tail.down = null;

            p1.up = null;
            p2.up = null;
            head = p1;
            tail = p2;
            height = height - 1;
        }
        //成功移除一个元素，大小减1
        size = size - 1;

        System.out.println("-----删除[" + key + "]后的跳跃表是:-----");
        printHorizontal();

    }

    /**
     * 打印出跳表的图示结构(水平方向)
     *
     * @author xxx 2017年2月14日 下午10:35:36
     * @since v1.0
     */
    public void printHorizontal() {
        String s = "";
        int i;
        SkipListEntry<V> p;

        p = head;

        while (p.down != null) {
            p = p.down;
        }

        i = 0;
        while (p != null) {
            p.pos = i++;
            p = p.right;
        }

        p = head;
        while (p != null) {
            s = getOneRow(p);
            System.out.println(s);
            p = p.down;
        }
    }

    private String getOneRow(SkipListEntry<V> p) {
        String s;
        int a, b, i;

        a = 0;

        s = "" + p.key;
        p = p.right;

        while (p != null) {
            SkipListEntry<V> q;

            q = p;
            while (q.down != null)
                q = q.down;
            b = q.pos;

            s = s + " <-";

            for (i = a + 1; i < b; i++)
                s = s + "--------";

            s = s + "> " + p.key;

            a = b;

            p = p.right;
        }

        return s;
    }

    /**
     * 打印出跳表的图示结构(垂直方向)
     *
     * @author xxx 2017年2月14日 下午10:35:36
     * @since v1.0
     */
    public void printVertical() {
        String s = "";
        SkipListEntry<V> p;
        p = head;
        while (p.down != null)
            p = p.down;

        while (p != null) {
            s = getOneColumn(p);
            System.out.println(s);

            p = p.right;
        }
    }

    private String getOneColumn(SkipListEntry<V> p) {
        String s = "";
        while (p != null) {
            s = s + " " + p.key;
            p = p.up;
        }
        return (s);
    }

}