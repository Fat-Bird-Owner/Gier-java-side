package GierJava;

import mindustry.mod.*;
import GierJava.content.*;
import GierJava.dialogs.*;
import GierJava.gen.*;
import mindustry.game.EventType;

public class GierJava extends Mod{
    @Override
    public void loadContent(){
        // Call this before loading any content!
        EntityRegistry.register();

        GierPlanets.load();
        GierUnitTypes.load();
        GierBlocks.load();
        GierTechTree.load();

    }

    @Override

    public void init(){

        researchDialog = new GierResearchDialog();

        Vars.ui.hudGroup.fill(table -> {
            table.button("Research", Icon.tree, () -> {
                showResearch();
            }).size(50f).tooltip("Open research tree");
        });
    }

    public static void showResearch(){
        if(researchDialog != null){
            researchDialog.show();
        }
    }

}
