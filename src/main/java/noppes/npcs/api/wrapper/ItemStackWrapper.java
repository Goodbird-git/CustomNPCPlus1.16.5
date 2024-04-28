package noppes.npcs.api.wrapper;

import net.minecraft.util.registry.Registry;
import noppes.npcs.api.item.*;
import net.minecraftforge.common.util.*;
import net.minecraft.inventory.*;
import noppes.npcs.api.entity.data.*;
import net.minecraft.nbt.*;
import com.google.common.collect.*;
import net.minecraft.block.*;
import net.minecraft.util.text.*;
import net.minecraftforge.registries.*;
import noppes.npcs.api.*;
import noppes.npcs.api.entity.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.enchantment.*;
import net.minecraftforge.common.*;
import noppes.npcs.entity.*;
import net.minecraft.util.*;
import net.minecraftforge.event.*;
import net.minecraftforge.common.capabilities.*;
import noppes.npcs.items.*;
import net.minecraft.item.*;
import java.util.*;
import com.google.gson.*;
import net.minecraft.entity.*;
import noppes.npcs.*;

public class ItemStackWrapper implements IItemStack, ICapabilitySerializable<CompoundNBT>
{
    private Map<String, Object> tempData;
    @CapabilityInject(ItemStackWrapper.class)
    public static Capability<ItemStackWrapper> ITEMSCRIPTEDDATA_CAPABILITY;
    private LazyOptional<ItemStackWrapper> instance;
    private static final EquipmentSlotType[] VALID_EQUIPMENT_SLOTS;
    public ItemStack item;
    private CompoundNBT storedData;
    public static ItemStackWrapper AIR;
    private final IData tempdata;
    private final IData storeddata;
    private static final ResourceLocation key;

    protected ItemStackWrapper(final ItemStack item) {
        this.tempData = new HashMap<>();
        this.instance = LazyOptional.of(() -> this);
        this.storedData = new CompoundNBT();
        this.tempdata = new IData() {
            @Override
            public void put(final String key, final Object value) {
                ItemStackWrapper.this.tempData.put(key, value);
            }

            @Override
            public Object get(final String key) {
                return ItemStackWrapper.this.tempData.get(key);
            }

            @Override
            public void remove(final String key) {
                ItemStackWrapper.this.tempData.remove(key);
            }

            @Override
            public boolean has(final String key) {
                return ItemStackWrapper.this.tempData.containsKey(key);
            }

            @Override
            public void clear() {
                ItemStackWrapper.this.tempData.clear();
            }

            @Override
            public String[] getKeys() {
                return ItemStackWrapper.this.tempData.keySet().toArray(new String[ItemStackWrapper.this.tempData.size()]);
            }
        };
        this.storeddata = new IData() {
            @Override
            public void put(final String key, final Object value) {
                if (value instanceof Number) {
                    ItemStackWrapper.this.storedData.putDouble(key, ((Number)value).doubleValue());
                }
                else if (value instanceof String) {
                    ItemStackWrapper.this.storedData.putString(key, (String)value);
                }
            }

            @Override
            public Object get(final String key) {
                if (!ItemStackWrapper.this.storedData.contains(key)) {
                    return null;
                }
                final INBT base = ItemStackWrapper.this.storedData.get(key);
                if (base instanceof NumberNBT) {
                    return ((NumberNBT)base).getAsDouble();
                }
                return base.getAsString();
            }

            @Override
            public void remove(final String key) {
                ItemStackWrapper.this.storedData.remove(key);
            }

            @Override
            public boolean has(final String key) {
                return ItemStackWrapper.this.storedData.contains(key);
            }

            @Override
            public void clear() {
                ItemStackWrapper.this.storedData = new CompoundNBT();
            }

            @Override
            public String[] getKeys() {
                return ItemStackWrapper.this.storedData.getAllKeys().toArray(new String[ItemStackWrapper.this.storedData.getAllKeys().size()]);
            }
        };
        this.item = item;
    }

    @Override
    public IData getTempdata() {
        return this.tempdata;
    }

    @Override
    public IData getStoreddata() {
        return this.storeddata;
    }

    @Override
    public int getStackSize() {
        return this.item.getCount();
    }

    @Override
    public void setStackSize(final int size) {
        if (size > this.getMaxStackSize()) {
            throw new CustomNPCsException("Can't set the stacksize bigger than MaxStacksize");
        }
        this.item.setCount(size);
    }

    @Override
    public void setAttribute(final String name, final double value) {
        this.setAttribute(name, value, -1);
    }

    @Override
    public void setAttribute(final String name, final double value, final int slot) {
        if (slot < -1 || slot > 5) {
            throw new CustomNPCsException("Slot has to be between -1 and 5, given was: " + slot);
        }
        CompoundNBT compound = this.item.getTag();
        if (compound == null) {
            this.item.setTag(compound = new CompoundNBT());
        }
        final ListNBT nbttaglist = compound.getList("AttributeModifiers", 10);
        final ListNBT newList = new ListNBT();
        for (int i = 0; i < nbttaglist.size(); ++i) {
            final CompoundNBT c = nbttaglist.getCompound(i);
            if (!c.getString("AttributeName").equals(name)) {
                newList.add(c);
            }
        }
        if (value != 0.0) {
            final CompoundNBT nbttagcompound = new AttributeModifier(name, value, AttributeModifier.Operation.ADDITION).save();
            nbttagcompound.putString("AttributeName", name);
            if (slot >= 0) {
                nbttagcompound.putString("Slot", EquipmentSlotType.values()[slot].getName());
            }
            newList.add(nbttagcompound);
        }
        compound.put("AttributeModifiers", newList);
    }

    @Override
    public double getAttribute(final String name) {
        final CompoundNBT compound = this.item.getTag();
        if (compound == null) {
            return 0.0;
        }
        final Multimap<Attribute, AttributeModifier> map = this.item.getAttributeModifiers(EquipmentSlotType.MAINHAND);
        for (final Map.Entry<Attribute, AttributeModifier> entry : map.entries()) {
            if (entry.getKey().getDescriptionId().equals(name)) {
                final AttributeModifier mod = entry.getValue();
                return mod.getAmount();
            }
        }
        return 0.0;
    }

    @Override
    public boolean hasAttribute(final String name) {
        final CompoundNBT compound = this.item.getTag();
        if (compound == null) {
            return false;
        }
        final ListNBT nbttaglist = compound.getList("AttributeModifiers", 10);
        for (int i = 0; i < nbttaglist.size(); ++i) {
            final CompoundNBT c = nbttaglist.getCompound(i);
            if (c.getString("AttributeName").equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addEnchantment(final String id, final int strenght) {
        final Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(id));
        if (ench == null) {
            throw new CustomNPCsException("Unknown enchant id:" + id);
        }
        enchant(item, ench, strenght);
    }

    public int getEnchantmentLevel(final String id) {
        final Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(id));
        if (ench == null) {
            throw new CustomNPCsException("Unknown enchant id:" + id);
        }
        if (!this.isEnchanted()) {
            return 0;
        }

        if (!item.getOrCreateTag().contains("Enchantments", 9)) {
            item.getOrCreateTag().put("Enchantments", new ListNBT());
        }

        ListNBT listnbt = item.getOrCreateTag().getList("Enchantments", 10);
        for (int i = 0; i < listnbt.size(); ++i) {
            final CompoundNBT compound = listnbt.getCompound(i);
            if(compound.getString("id").equals(String.valueOf(Registry.ENCHANTMENT.getKey(ench)))){
                return compound.getInt("lvl");
            }
        }
        return 0;
    }

    public void enchant(ItemStack stack, Enchantment p_77966_1_, int p_77966_2_) {
        if (!stack.getOrCreateTag().contains("Enchantments", 9)) {
            stack.getOrCreateTag().put("Enchantments", new ListNBT());
        }

        ListNBT listnbt = stack.getOrCreateTag().getList("Enchantments", 10);
        for (int i = 0; i < listnbt.size(); ++i) {
            final CompoundNBT compound = listnbt.getCompound(i);
            if(compound.getString("id").equals(String.valueOf(Registry.ENCHANTMENT.getKey(p_77966_1_)))){
                compound.putShort("lvl", (byte)p_77966_2_);
                return;
            }
        }
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(p_77966_1_)));
        compoundnbt.putShort("lvl", ((byte)p_77966_2_));
        listnbt.add(compoundnbt);
    }

    @Override
    public boolean isEnchanted() {
        return this.item.isEnchanted();
    }

    @Override
    public boolean hasEnchant(final String id) {
        final Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(id));
        if (ench == null) {
            throw new CustomNPCsException("Unknown enchant id:" + id);
        }
        if (!this.isEnchanted()) {
            return false;
        }
        final ListNBT list = this.item.getEnchantmentTags();
        for (int i = 0; i < list.size(); ++i) {
            final CompoundNBT compound = list.getCompound(i);
            if (compound.getString("id").equalsIgnoreCase(String.valueOf(Registry.ENCHANTMENT.getKey(ench)))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeEnchant(final String id) {
        final Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(id));
        if (ench == null) {
            throw new CustomNPCsException("Unknown enchant id:" + id);
        }
        if (!this.isEnchanted()) {
            return false;
        }
        final ListNBT list = this.item.getEnchantmentTags();
        final ListNBT newList = new ListNBT();
        for (int i = 0; i < list.size(); ++i) {
            final CompoundNBT compound = list.getCompound(i);
            if (!compound.getString("id").equalsIgnoreCase(String.valueOf(Registry.ENCHANTMENT.getKey(ench)))) {
                newList.add(compound);
            }
        }
        if (list.size() == newList.size()) {
            return false;
        }
        this.item.getOrCreateTag().put("ench", newList);
        return true;
    }

    @Override
    public boolean isBlock() {
        final Block block = Block.byItem(this.item.getItem());
        return block != Blocks.AIR;
    }

    @Override
    public boolean hasCustomName() {
        return this.item.hasCustomHoverName();
    }

    @Override
    public void setCustomName(final String name) {
        this.item.setHoverName(new TranslationTextComponent(name));
    }

    @Override
    public String getDisplayName() {
        return this.item.getHoverName().getString();
    }

    @Override
    public String getItemName() {
        return this.item.getItem().getName(this.item).getString();
    }

    @Override
    public String getName() {
        return ForgeRegistries.ITEMS.getKey(this.item.getItem()).toString();
    }

    @Override
    public INbt getNbt() {
        CompoundNBT compound = this.item.getTag();
        if (compound == null) {
            this.item.setTag(compound = new CompoundNBT());
        }
        return NpcAPI.Instance().getINbt(compound);
    }

    @Override
    public boolean hasNbt() {
        final CompoundNBT compound = this.item.getTag();
        return compound != null && !compound.isEmpty();
    }

    @Override
    public ItemStack getMCItemStack() {
        return this.item;
    }

    public static ItemStack MCItem(final IItemStack item) {
        if (item == null) {
            return ItemStack.EMPTY;
        }
        return item.getMCItemStack();
    }

    @Override
    public void damageItem(final int damage, final IMob living) {
        if (living != null) {
            this.item.hurtAndBreak(damage, (LivingEntity)((living == null) ? null : living.getMCEntity()), e -> e.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
        }
        else if (this.item.isDamageableItem()) {
            if (this.item.getDamageValue() <= damage) {
                this.item.shrink(1);
                this.item.setDamageValue(0);
            }
            else {
                this.item.setDamageValue(this.item.getDamageValue() - damage);
            }
        }
    }

    @Override
    public boolean isBook() {
        return false;
    }

    @Override
    public int getFoodLevel() {
        if (this.item.getItem().getFoodProperties() != null) {
            return this.item.getItem().getFoodProperties().getNutrition();
        }
        return 0;
    }

    @Override
    public IItemStack copy() {
        return createNew(this.item.copy());
    }

    @Override
    public int getMaxStackSize() {
        return this.item.getMaxStackSize();
    }

    @Override
    public boolean isDamageable() {
        return this.item.isDamageableItem();
    }

    @Override
    public int getDamage() {
        return this.item.getDamageValue();
    }

    @Override
    public void setDamage(final int value) {
        this.item.setDamageValue(value);
    }

    @Deprecated
    public int getItemDamage() {
        return this.item.getDamageValue();
    }

    @Deprecated
    public void setItemDamage(final int value) {
        this.item.setDamageValue(value);
    }

    @Override
    public int getMaxDamage() {
        return this.item.getMaxDamage();
    }

    @Override
    public INbt getItemNbt() {
        final CompoundNBT compound = new CompoundNBT();
        this.item.save(compound);
        return NpcAPI.Instance().getINbt(compound);
    }

    @Override
    public double getAttackDamage() {
        final Multimap<Attribute, AttributeModifier> map = this.item.getAttributeModifiers(EquipmentSlotType.MAINHAND);
        double damage = 0.0;
        for (final Map.Entry<Attribute, AttributeModifier> entry : map.entries()) {
            if (entry.getKey() == Attributes.ATTACK_DAMAGE) {
                final AttributeModifier mod = entry.getValue();
                damage = mod.getAmount();
            }
        }
        return damage + EnchantmentHelper.getDamageBonus(this.item, CreatureAttribute.UNDEFINED);
    }

    @Override
    public boolean isEmpty() {
        return this.item.isEmpty();
    }

    @Override
    public int getType() {
        if (this.item.getItem() instanceof IPlantable) {
            return 5;
        }
        if (this.item.getItem() instanceof SwordItem) {
            return 4;
        }
        return 0;
    }

    @Override
    public boolean isWearable() {
        for (final EquipmentSlotType slot : ItemStackWrapper.VALID_EQUIPMENT_SLOTS) {
            if (this.item.getItem().canEquip(this.item, slot, EntityNPCInterface.CommandPlayer)) {
                return true;
            }
        }
        return false;
    }

    public <T> LazyOptional<T> getCapability(final Capability<T> capability, final Direction facing) {
        if (capability == ItemStackWrapper.ITEMSCRIPTEDDATA_CAPABILITY) {
            return this.instance.cast();
        }
        return LazyOptional.empty();
    }

    public static void register(final AttachCapabilitiesEvent<ItemStack> event) {
        final ItemStackWrapper wrapper = createNew(event.getObject());
        event.addCapability(ItemStackWrapper.key, wrapper);
    }

    private static ItemStackWrapper createNew(final ItemStack item) {
        if (item == null || item.isEmpty()) {
            return ItemStackWrapper.AIR;
        }
        if (item.getItem() instanceof ItemScripted) {
            return new ItemScriptedWrapper(item);
        }
        if (item.getItem() == Items.WRITTEN_BOOK || item.getItem() == Items.WRITABLE_BOOK || item.getItem() instanceof WritableBookItem || item.getItem() instanceof WrittenBookItem) {
            return new ItemBookWrapper(item);
        }
        if (item.getItem() instanceof ArmorItem) {
            return new ItemArmorWrapper(item);
        }
        final Block block = Block.byItem(item.getItem());
        if (block != Blocks.AIR) {
            return new ItemBlockWrapper(item);
        }
        return new ItemStackWrapper(item);
    }

    @Override
    public String[] getLore() {
        final CompoundNBT compound = this.item.getTagElement("display");
        if (compound == null || compound.getTagType("Lore") != 9) {
            return new String[0];
        }
        final ListNBT nbttaglist = compound.getList("Lore", 8);
        if (nbttaglist.isEmpty()) {
            return new String[0];
        }
        final List<String> lore = new ArrayList<>();
        for (int i = 0; i < nbttaglist.size(); ++i) {
            lore.add(nbttaglist.getString(i));
        }
        return lore.toArray(new String[lore.size()]);
    }

    @Override
    public void setLore(final String[] lore) {
        final CompoundNBT compound = this.item.getOrCreateTagElement("display");
        if (lore == null || lore.length == 0) {
            compound.remove("Lore");
            return;
        }
        final ListNBT nbtlist = new ListNBT();
        for (String s : lore) {
            try {
                ITextComponent.Serializer.fromJson(s);
            }
            catch (JsonParseException jsonparseexception) {
                s = ITextComponent.Serializer.toJson(new TranslationTextComponent(s));
            }
            nbtlist.add(StringNBT.valueOf(s));
        }
        compound.put("Lore", nbtlist);
    }

    public CompoundNBT serializeNBT() {
        return this.getMCNbt();
    }

    public void deserializeNBT(final CompoundNBT nbt) {
        this.setMCNbt(nbt);
    }

    public CompoundNBT getMCNbt() {
        final CompoundNBT compound = new CompoundNBT();
        if (!this.storedData.isEmpty()) {
            compound.put("StoredData", this.storedData);
        }
        return compound;
    }

    public void setMCNbt(final CompoundNBT compound) {
        this.storedData = compound.getCompound("StoredData");
    }

    @Override
    public void removeNbt() {
        this.item.setTag(null);
    }

    @Override
    public boolean compare(IItemStack item, final boolean ignoreNBT) {
        if (item == null) {
            item = ItemStackWrapper.AIR;
        }
        return NoppesUtilPlayer.compareItems(this.getMCItemStack(), item.getMCItemStack(), false, ignoreNBT);
    }

    static {
        ItemStackWrapper.ITEMSCRIPTEDDATA_CAPABILITY = null;
        VALID_EQUIPMENT_SLOTS = new EquipmentSlotType[] { EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET };
        ItemStackWrapper.AIR = new ItemStackEmptyWrapper();
        key = new ResourceLocation("customnpcs", "itemscripteddata");
    }
}
