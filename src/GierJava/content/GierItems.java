package GierJava.content;

import arc.graphics.*;
import arc.struct.*;
import mindustry.type.*;

public class GierItems{
    public static Item metaglassChunk;

    public static void load(){

        metaglassChunk = new Item("metaglass-chunk", Color.valueOf("ebeff5")){{
            cost = 1.5f;
        }};

    }
}