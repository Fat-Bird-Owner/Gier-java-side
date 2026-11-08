package GierJava.ui.dialogs;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.event.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;

public class ArchiveResearchDialog{

    private final AudioBus bus = new AudioBus();
    private final BaseDialog dialog = new BaseDialog("Files");

    private Planet previous;
    private Planet planet;
    private boolean opening;

    public ArchiveResearchDialog(){
        dialog.addCloseButton();
        Events.run(Trigger.update, this::update);
    }

    private void playSound(Sound sound){
        AudioBus previousBus = sound.bus;
        sound.setBus(bus);
        sound.play();
        sound.setBus(previousBus);
    }

    private Planet getPlanet(){
        if(Vars.ui.research.isShown()){
            TechTree.TechNode node = Vars.ui.research.lastNode;

            if(node != null){
                if(node.planet != null) return node.planet;

                for(Planet p : Vars.content.planets()){
                    if(p.techTree == node) return p;
                }
            }
        }

        if(Vars.ui.planet.isShown()){
            return Vars.ui.planet.state.planet;
        }

        if(Vars.state.isCampaign() && Vars.state.rules.sector != null){
            return Vars.state.rules.sector.planet;
        }

        return Vars.state.rules.planet;
    }

    private boolean supported(Planet planet){
        return planet != null &&
            (planet.name.equals("gr-gier") || planet.name.equals("gr-kela"));
    }

    private boolean valid(TechTree.TechNode node){
        for(Objectives.Objective objective : node.objectives){
            try{
                if(!objective.complete()) return false;
            }catch(Exception ignored){}
        }

        return true;
    }

    private String objectiveString(TechTree.TechNode node){
        StringBuilder string = new StringBuilder();

        for(Objectives.Objective objective : node.objectives){
            try{
                string.append(objective.complete() ? "[green]" : "[red]")
                    .append("\n - ")
                    .append(objective);
            }catch(Exception ignored){}
        }

        return string.toString();
    }

    private ItemSeq getResearchItems(){
        ItemSeq items = new ItemSeq();

        if(planet == null) return items;

        for(Sector sector : planet.sectors){
            if(sector.hasBase() && !sector.isFrozen()){
                sector.items().each((item, amount) ->
                    items.add(item, Math.max(amount, 0))
                );
            }
        }

        return items;
    }

    private boolean requirementsComplete(TechTree.TechNode node){
        for(int i = 0; i < node.requirements.length; i++){
            if(node.finishedRequirements[i].amount < node.requirements[i].amount){
                return false;
            }
        }

        return true;
    }

    private void canUnlock(TechTree.TechNode node){
        if(!requirementsComplete(node) || !valid(node)) return;

        if(!node.content.unlocked()){
            playSound(Sounds.uiUnlock);
        }

        node.content.quietUnlock();
    }

    private void update(){
        if(opening || !Core.settings.getBool("research-custom")) return;
        if(!Vars.ui.research.isShown()) return;

        Planet current = getPlanet();

        if(!supported(current)) return;

        previous = current;
        opening = true;

        rebuild();
        dialog.show();

        Time.runTask(1f, () -> {
            Vars.ui.research.hide();
            opening = false;
        });
    }

    private void rebuild(){
        dialog.cont.clear();

        planet = getPlanet();

        if(planet == null || planet.techTree == null) return;

        dialog.cont.pane(p -> {
            p.clear();

            Button back = new Button();
            back.add("@back");
            p.add(back).size(150f, 50f).row();

            back.clicked(() -> {
                dialog.hide();

                Vars.ui.research.rebuildTree(
                    previous != null && previous.techTree != null
                        ? previous.techTree
                        : Planets.serpulo.techTree
                );

                Vars.ui.research.show();
            });

            ItemsDisplay display = new ItemsDisplay();
            display.rebuild(getResearchItems());
            p.add(display).row();

            planet.techTree.each(node -> {
                Table table = new Table();
                rebuildTable(node, table);
                p.add(table).pad(5f).height(150f).growX().row();
            });
        }).grow();
    }

    private Drawable getTypeIcon(UnlockableContent content){
        if(content instanceof Block) return Icon.effect;
        if(content instanceof UnitType) return Icon.units;
        return Icon.sitemap;
    }

    private void rebuildTable(TechTree.TechNode node, Table table){
        table.clear();
        table.background(Tex.whiteui);
        table.setColor(Pal.darkerGray);

        boolean parentUnlocked = node.parent == null ||
            node.parent.content.unlocked();

        boolean objectivesComplete = valid(node);

        if(parentUnlocked && objectivesComplete){
            Image typeImage = new Image(getTypeIcon(node.content));
            Image image = new Image(node.content.uiIcon);
            image.setScaling(Scaling.fit);

            Label label = new Label(node.content.localizedName);

            typeImage.setScaling(Scaling.fit);

            image.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    Vars.ui.content.show(node.content);
                }
            });

            label.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    Vars.ui.content.show(node.content);
                }
            });

            Button research = new Button();
            research.add(new Image(Icon.tree));

            table.add(typeImage).pad(15f);
            table.add(image).size(48f).pad(20f);
            table.add(label).pad(20f);
            table.add(research).size(50f).padRight(350f);

            if(!node.content.unlocked()){
                image.setColor(Pal.gray);
                table.setColor(Pal.darkestGray);
            }

            research.clicked(() -> {
                try{
                    playSound(Sounds.uiButton);
                    canUnlock(node);

                    if(!node.content.unlocked() || !valid(node)){
                        showRequirements(node);
                        return;
                    }

                    Vars.ui.content.show(node.content);
                }catch(Exception e){
                    Log.err(e);
                }
            });
        }else{
            Button button = new Button(Styles.nonet);
            boolean showObjectives = parentUnlocked && !objectivesComplete;

            button.image(showObjectives ? Icon.none : Icon.tree)
                .color(showObjectives ? Color.black : Color.white);

            table.setColor(
                showObjectives ? Pal.removeBack : Pal.darkestestGray
            );

            table.add(button);

            if(showObjectives){
                button.clicked(() -> showObjectives(node));
            }
        }
    }

    private void showObjectives(TechTree.TechNode node){
        BaseDialog objectives = new BaseDialog("@objectives");
        objectives.addCloseButton();
        objectives.cont.add(objectiveString(node));
        objectives.show();
    }

    private void showRequirements(TechTree.TechNode node){
        BaseDialog research = new BaseDialog("@item");
        research.addCloseButton();

        research.cont.pane(p -> {
            ItemsDisplay display = new ItemsDisplay();
            display.rebuild(getResearchItems());
            p.add(display).row();

            for(int i = 0; i < node.requirements.length; i++){
                final int index = i;

                ItemStack requirement = node.requirements[i];
                Item item = requirement.item;
                int amount = requirement.amount;

                Table table = new Table();

                Label progress = new Label(
                    "[grey](" + node.finishedRequirements[i].amount + ")"
                );

                Button add = new Button();
                add.add(new Image(Icon.add));

                table.add(new Image(item.uiIcon));
                table.add(item.localizedName);
                table.add(String.valueOf(amount)).pad(25f);
                table.add(progress).padRight(25f);

                if(node.finishedRequirements[i].amount < amount){
                    table.add(add);

                    add.clicked(() -> {
                        if(Vars.net.client()) return;

                        try{
                            playSound(Sounds.uiChat);

                            int remaining = amount -
                                node.finishedRequirements[index].amount;

                            if(remaining <= 0) return;

                            for(Sector sector : planet.sectors){
                                if(remaining <= 0) break;
                                if(!sector.hasBase() || sector.isFrozen()) continue;

                                int available = sector.items().get(item);
                                if(available <= 0) continue;

                                int used = Math.min(available, remaining);

                                sector.removeItem(item, used);

                                node.finishedRequirements[index].amount += used;
                                remaining -= used;
                            }

                            node.save();

                            display.rebuild(getResearchItems());

                            progress.setText(
                                "[grey](" +
                                node.finishedRequirements[index].amount +
                                ")"
                            );

                            canUnlock(node);
                            rebuild();

                            if(node.finishedRequirements[index].amount >= amount){
                                add.remove();
                            }
                        }catch(Exception e){
                            Log.err(e);
                        }
                    });
                }

                table.background(Tex.whiteui);
                table.setColor(Pal.darkerGray);

                p.add(table).pad(5f).growX().row();
            }
        }).grow();

        research.show();
    }
}
