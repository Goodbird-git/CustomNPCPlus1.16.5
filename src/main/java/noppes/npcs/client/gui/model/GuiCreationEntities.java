package noppes.npcs.client.gui.model;

import noppes.npcs.entity.*;
import net.minecraft.world.*;
import net.minecraftforge.registries.*;
import net.minecraft.entity.*;
import java.util.*;
import noppes.npcs.shared.client.gui.listeners.*;
import net.minecraft.client.gui.screen.*;
import java.util.function.*;
import java.util.stream.*;
import noppes.npcs.*;
import noppes.npcs.shared.client.gui.components.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.gui.widget.button.*;

public class GuiCreationEntities extends GuiCreationScreenInterface implements ICustomScrollListener
{
    private List<EntityType<? extends Entity>> types;
    private GuiCustomScroll scroll;
    private boolean resetToSelected;

    public GuiCreationEntities(final EntityNPCInterface npc) {
        super(npc);
        this.resetToSelected = true;
        Collections.sort(this.types = getAllEntities(npc.level), Comparator.comparing(t -> t.getDescriptionId().toLowerCase()));
        this.active = 1;
        this.xOffset = 60;
    }

    private static List<EntityType<? extends Entity>> getAllEntities(final World level) {
        final List<EntityType<? extends Entity>> data = new ArrayList<EntityType<? extends Entity>>();
        for (final EntityType<? extends Entity> ent : ForgeRegistries.ENTITIES.getValues()) {
            try {
                final Entity e = ent.create(level);
                if (e == null) {
                    continue;
                }
                if (LivingEntity.class.isAssignableFrom(e.getClass())) {
                    data.add(ent);
                }
                e.remove();
                e.removed = true;
            }
            catch (Exception ex) {}
        }
        return data;
    }

    @Override
    public void init() {
        super.init();
        this.addButton(new GuiButtonNop(this, 10, this.guiLeft, this.guiTop + 46, 120, 20, "Reset To NPC", button -> {
            this.playerdata.setEntity((String)null);
            this.npc.display.setSkinTexture("customnpcs:textures/entity/humanmale/steve.png");
            this.resetToSelected = true;
            this.init();
        }));
        if (this.scroll == null) {
            (this.scroll = new GuiCustomScroll(this, 0)).setUnsortedList(this.types.stream().map(EntityType::getDescriptionId).collect(Collectors.toList()));
        }
        this.scroll.guiLeft = this.guiLeft;
        this.scroll.guiTop = this.guiTop + 68;
        this.scroll.setSize(120, this.imageHeight - 96);
        int index = -1;
        EntityType selectedType = CustomEntities.entityCustomNpc;
        if (this.entity != null) {
            for (int i = 0; i < this.types.size(); ++i) {
                final EntityType type = this.types.get(i);
                if (type == this.entity.getType()) {
                    index = i;
                    selectedType = type;
                    break;
                }
            }
        }
        if (index >= 0) {
            this.scroll.setSelectedIndex(index);
        }
        else {
            this.scroll.setSelected("entity.customnpcs.customnpc");
        }
        if (this.resetToSelected) {
            this.scroll.scrollTo(this.scroll.getSelected());
            this.resetToSelected = false;
        }
        this.addScroll(this.scroll);
        this.addLabel(new GuiLabel(110, "gui.simpleRenderer", this.guiLeft + 124, this.guiTop + 5, 16711680));
        this.addButton(new GuiButtonYesNo(this, 110, this.guiLeft + 260, this.guiTop, this.playerdata.simpleRender, b -> this.playerdata.simpleRender = ((GuiButtonYesNo)b).getBoolean()));
    }

    @Override
    public void scrollClicked(final double i, final double j, final int k, final GuiCustomScroll scroll) {
        final String selected = scroll.getSelected();
        if (selected.equals("entity.customnpcs.customnpc")) {
            this.playerdata.setEntity((String)null);
        }
        else {
            this.playerdata.setEntity(this.types.get(scroll.getSelectedIndex()).getRegistryName());
        }
        final Entity entity = (Entity)this.playerdata.getEntity(this.npc);
        if (entity != null) {
            final EntityRenderer render = this.minecraft.getEntityRenderDispatcher().getRenderer(entity);
            if (render instanceof LivingRenderer && !NPCRendererHelper.getTexture(render, entity).equals("minecraft:missingno")) {
                this.npc.display.setSkinTexture(NPCRendererHelper.getTexture(render, entity));
            }
        }
        else {
            this.npc.display.setSkinTexture("customnpcs:textures/entity/humanmale/steve.png");
        }
        this.init();
    }

    @Override
    public void scrollDoubleClicked(final String selection, final GuiCustomScroll scroll) {
    }
}
