package ca.uberifix.functionalaesthetics.common.block.rustic;

import ca.uberifix.functionalaesthetics.common.block.BlockVariants;
import ca.uberifix.functionalaesthetics.common.tileentity.rustic.CampfireTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by uberifix
 */
public class StoneCampfire2Block extends BlockRustic implements ITileEntityProvider{
    public static final PropertyEnum<BlockVariants.EnumStoneVariant> STONE_VARIANT = PropertyEnum.create("stone_variant", BlockVariants.EnumStoneVariant.class);
    public static final PropertyEnum<BlockVariants.EnumWoodVariantNew> WOOD_VARIANT = PropertyEnum.create("wood_variant", BlockVariants.EnumWoodVariantNew.class);
    private static final AxisAlignedBB CAMPFIRE_AABB = new AxisAlignedBB(0.2D, 0.0D, 0.2D, 0.8D, 0.4D, 0.8D);

    public StoneCampfire2Block() {
        super("campfire_pit_2", Material.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(STONE_VARIANT, BlockVariants.EnumStoneVariant.STONE).withProperty(WOOD_VARIANT, BlockVariants.EnumWoodVariantNew.DARK_OAK));
        this.setLightLevel(1.0F);
        this.setHardness(3.0F);
        this.setHarvestLevel("axe", 0);
        translucent = true;
        this.registerBlock();
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return CAMPFIRE_AABB;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return 15;
    }

    public boolean hasGround(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return false;
        } else { return true; }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.hasGround(worldIn, pos, state);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess worldIn, BlockPos pos, IBlockState state, int fortune) {
        int woodmeta = worldIn.getBlockState(pos).getBlock().getMetaFromState(worldIn.getBlockState(pos)) & 0x0c;
        int stonemeta = worldIn.getBlockState(pos).getBlock().getMetaFromState(worldIn.getBlockState(pos)) & 0x03;
        ArrayList<ItemStack> drops = new ArrayList<>();
        if (RANDOM.nextFloat() < 0.25F) { drops.add(new ItemStack(Items.COAL, 1, 1)); }
        if (RANDOM.nextFloat() < 0.5F) { drops.add(new ItemStack(Blocks.PLANKS, RANDOM.nextInt(2) + 1, (woodmeta >> 2 ) + 4)); }
        if (stonemeta == 0) { drops.add(new ItemStack(Blocks.COBBLESTONE, 1, stonemeta)); }
        else { drops.add(new ItemStack(Blocks.STONE, 1, (stonemeta * 2) - 1)); }
        return drops;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        double d0 = (double)pos.getX();
        double d1 = (double)pos.getY() + 0.5D;
        double d2 = (double)pos.getZ();
        for (int i = 0; i < 4; i++) {
            double rand1 = Math.random() * 0.3;
            double rand2 = Math.random() * 0.3;
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0 + 0.5, d1, d2 + 0.5, 0.0D, 0.01D, 0.0D, new int[0]);
            worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + rand1 + 0.35, d1, d2 + rand2 + 0.35, 0.0D, 0.01D, 0.0D, new int[0]);
        }

        if (rand.nextInt(12) == 0)
        {
            worldIn.playSound((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        entityIn.setFire(10);
    }

    public int damageDropped(IBlockState state) { return getMetaFromState(state); }

    public IBlockState getStateFromMeta(int meta) {
        int stone_bits = (meta & 0x03);
        int wood_bits = (meta & 0x0c) >> 2;
        BlockVariants.EnumStoneVariant stone = BlockVariants.EnumStoneVariant.byMetadata(stone_bits);
        BlockVariants.EnumWoodVariantNew wood = BlockVariants.EnumWoodVariantNew.byMetadata(wood_bits);
        return this.getDefaultState().withProperty(STONE_VARIANT, stone).withProperty(WOOD_VARIANT, wood);
    }

    public int getMetaFromState(IBlockState state) {
        BlockVariants.EnumStoneVariant stone = state.getValue(STONE_VARIANT);
        BlockVariants.EnumWoodVariantNew wood = state.getValue(WOOD_VARIANT);
        int stone_bits = stone.getMetadata();
        int wood_bits = wood.getMetadata() << 2;
        return wood_bits | stone_bits;
    }

    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, STONE_VARIANT, WOOD_VARIANT); }

    public TileEntity createNewTileEntity(World worldIn, int meta) { return new CampfireTileEntity(); }
}