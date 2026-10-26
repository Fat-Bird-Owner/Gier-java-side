package GierJava.world.blocks;

import arc.util.io.Reads; 
import arc.util.io.Writes; 
import mindustry.world.Block; 
import mindustry.gen.Building; 
import mindustry.type.Item; 
import mindustry.type.ItemStack; 
import mindustry.content.Items; 
import mindustry.world.meta.*; 
import mindustry.world.Block;
import arc.Core;
import arc.scene.ui.layout.Table;
import arc.scene.ui.*;
import mindustry.gen.Unit;
import mindustry.ui.Styles;
import mindustry.world.draw.*;

public class MultiCrafter extends Block{

    public DrawBlock drawer = new DrawDefault();

    public static class Recipe{
        public ItemStack[] input;
        public ItemStack[] output;
        public String icon = "null";
        public float craftTime = 60f;

    public Recipe(ItemStack[] input, ItemStack[] output, float craftTime, String icon){
        this.input = input;
        this.output = output;
        this.craftTime = craftTime;
        this.icon = icon;
    }}

    public Recipe[] recipes;
    public int defaultRecipe = 0;

    @Override 
    public void load(){
        super.load();
        drawer.load(this);
    }

    @Override
        public void setStats(){
            super.setStats();

            stats.add(Stat.output, table -> {
                if(recipes == null) return;

                for(int i = 0; i < recipes.length; i++){

                    final int index = i;
                table.table(Styles.black3, cont -> {

                    Recipe recipe = recipes[index];

                    cont.add("[accent]Recipe " + (index + 1) + "[]").left();
                    cont.row();

                    cont.add("[lightgray]Input:[]").left();
                    cont.row();

                    for(ItemStack stack : recipe.input){
                        cont.image(stack.item.uiIcon).size(24f);
                        cont.add(" " + stack.amount + "x " + stack.item.localizedName).left();
                        cont.row();
                    }

                        cont.add("[lightgray]Output:[]").left();
                        cont.row();

                            for(ItemStack stack : recipe.output){
                                    cont.image(stack.item.uiIcon).size(24f);
                                    cont.add(" " + stack.amount + "x " + stack.item.localizedName);
                                    cont.row();
                            }

                        cont.add("[lightgray]Craft time:[] " + recipe.craftTime / 60f + " seconds").left();
                }).pad(6f).row();
            }

        });
    }

public MultiCrafter(String name){
    super(name);
    update = true;
    solid = true;
    hasItems = true;
    itemCapacity = 20;
    configurable = true;
    saveConfig = true;
}

public class MultiCrafterBuild extends Building{
    public int recipeIndex = defaultRecipe;
    public float progress;

    @Override 
    public void draw(){
        drawer.draw(this);
    }

    public Recipe recipe(){
        if(recipes == null || recipes.length == 0) return null;
        return recipes[Math.max(0, Math.min(recipeIndex, recipes.length - 1))];
    }

    public boolean canCraft(Recipe r){
        if(r == null) return false;

        for(ItemStack stack : r.input){
            if(items.get(stack.item) < stack.amount) return false;
        }

        for(ItemStack stack : r.output){
            if(items.get(stack.item) + stack.amount > itemCapacity) return false;
        }

        return true;
    }

    @Override
    public void updateTile(){
        Recipe r = recipe();
        dumpOutputs(r);

        if(!canCraft(r)) return;
        progress += edelta();

        if(progress >= r.craftTime){
            for(ItemStack stack : r.input){
                items.remove(stack.item, stack.amount);
            }

            for(ItemStack stack : r.output){
                items.add(stack.item, stack.amount);
            }

            progress = 0f;
        }

    }

    public void dumpOutputs(Recipe r){
        for (ItemStack stack : r.output){
            dump(stack.item);
        }
    }

    @Override
    public boolean acceptItem(Building source, Item item){
            Recipe r = recipe();
            if(r == null) return false;

            for(ItemStack stack : r.input){
                    if(stack.item == item && items.get(item) < itemCapacity){
                            return true;
                    }
            }

            return false;
    }

    @Override
    public int getMaximumAccepted(Item item){
        return itemCapacity;
    }

    @Override
    public void buildConfiguration(Table table){
        for(int i = 0; i < recipes.length; i++){
            final int index = i;

            final Button button = new Button();
            button.add(new Image(Core.atlas.find(recipes[i].icon)));
            table.add(button).size(50f);

            button.clicked(() -> {
                configure(index);
            });

        }
    }

    @Override
    public float warmup(){
        return canCraft(recipe()) ? 1f : 0f;
    }

    @Override
    public void configured(Unit unit, Object value){
        if(value instanceof Integer){
            recipeIndex = (int)value;
            progress = 0f;
        }
    }

    @Override
    public Object config(){
        return recipeIndex;
    }

    @Override
    public void write(Writes write){
        super.write(write);
        write.i(recipeIndex);
        write.f(progress);
    }

    @Override
    public void read(Reads read, byte revision){
        super.read(read, revision);
        recipeIndex = read.i();
        progress = read.f();
    }
}}