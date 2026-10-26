package GierJava.world.blocks;

import arc.util.io.Reads; 
import arc.util.io.Writes; 
import mindustry.world.Block; 
import mindustry.gen.Building; 
import mindustry.type.Item; 
import mindustry.type.ItemStack; 
import mindustry.content.Items; 
import mindustry.world.meta.BuildVisibility; 
import mindustry.world.Block;
import arc.Core;
import arc.scene.ui.layout.Table;
import arc.scene.ui.Image;
import mindustry.gen.Unit;

public class MultiCrafter extends Block{

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
        public void setStats(){
            super.setStats();

            stats.add(Stat.output, table -> {
                if(recipes == null) return;

                for(int i = 0; i < recipes.length; i++){
                    Recipe recipe = recipes[i];

                    table.row();
                    table.add("[accent]Recipe " + (i + 1) + "[]").left();
                    table.row();

                    table.add("[lightgray]Input:[]").left();
                    table.row();

                    for(ItemStack stack : recipe.input){
                        table.image(stack.item.uiIcon).size(24f);
                        table.add(" " + stack.amount + "x " + stack.item.localizedName).left();
                        table.row();
                    }

                        table.add("[lightgray]Output:[]").left();
                        table.row();

                            for(ItemStack stack : recipe.output){
                                    table.image(stack.item.uiIcon).size(24f);
                                    table.add(" " + stack.amount + "x " + stack.item.localizedName).left();
                                    table.row();
                            }

                        table.add("[lightgray]Craft time:[] " + recipe.craftTime / 60f + " seconds").left();
                        table.row();
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

    @Override
    public void buildConfiguration(Table table){
        for(int i = 0; i < recipes.length; i++){
            final int index = i;

            table.button(new Image(ore.atlas.find(recipes[i].icon)), () -> {
                configure(index);
            }).size(120f, 50f);
        }
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