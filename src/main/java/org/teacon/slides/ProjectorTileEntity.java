package org.teacon.slides;

import java.util.Objects;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ObjectHolder;

public final class ProjectorTileEntity extends TileEntity {

    @ObjectHolder("slide_show:projector")
    public static TileEntityType<ProjectorTileEntity> theType;

    public SlideData currentSlide = new SlideData();

    public ProjectorTileEntity() {
        super(Objects.requireNonNull(theType));
    }

    public CompoundNBT writeOurData(CompoundNBT data) {
        return SlideDataUtils.writeTo(this.currentSlide, data);
    }

    public void readOurData(CompoundNBT data) {
        SlideDataUtils.readFrom(this.currentSlide, data);
    }

    @Override
    public CompoundNBT write(CompoundNBT data) {
        return super.write(this.writeOurData(data));
    }

    @Override
    public void read(BlockState state, CompoundNBT data) {
        super.read(state, data);
        this.readOurData(data);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.writeOurData(new CompoundNBT()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.readOurData(packet.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag)
    {
        this.read(state, tag);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        // Vanilla structure block has similar issue of "rendering at any far distance",
        // so we just throw it here as a bandaid solution.
        // TODO: See if we can narrow down it
        return INFINITE_EXTENT_AABB;
    }
}
